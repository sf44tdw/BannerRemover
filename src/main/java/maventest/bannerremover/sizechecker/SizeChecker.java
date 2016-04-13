package maventest.bannerremover.sizechecker;

/**
 * 壊れたファイルを読み込むとこんなエラーが出る。 at
 * java.util.ArrayList.elementData(ArrayList.java:418) at
 * java.util.ArrayList.get(ArrayList.java:431) at
 * com.sun.imageio.plugins.jpeg.JPEGImageReader.checkTablesOnly(JPEGImageReader.java:378)
 * at
 * com.sun.imageio.plugins.jpeg.JPEGImageReader.gotoImage(JPEGImageReader.java:481)
 * at
 * com.sun.imageio.plugins.jpeg.JPEGImageReader.readHeader(JPEGImageReader.java:602)
 * at
 * com.sun.imageio.plugins.jpeg.JPEGImageReader.readInternal(JPEGImageReader.java:1059)
 * at
 * com.sun.imageio.plugins.jpeg.JPEGImageReader.read(JPEGImageReader.java:1039)
 * at javax.imageio.ImageIO.read(ImageIO.java:1448) at
 * javax.imageio.ImageIO.read(ImageIO.java:1308)
 */
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SizeChecker {

    private final Log log = LogFactory.getLog(SizeChecker.class);
    private final List<File> ImageList;
    private Set<ImageSize> sizes;

    /**
     * 渡されたファイルリストに載っている画像ファイルのピクセル数が特定のサイズであるかチェックし、一致したファイルのみをリストにする。
     *
     * @param ImageList ファイルリスト
     */
    public SizeChecker(List<File> ImageList) {
        this.ImageList = ImageList;
    }

    public Set<ImageSize> getSizes() {
        return sizes;
    }

    public void setSizes(Set<ImageSize> sizes) {
        Set<ImageSize> temp = new HashSet<>();
        temp.addAll(sizes);
        this.sizes = Collections.unmodifiableSet(temp);
    }

    private final MessageFormat info = new MessageFormat("ファイルのパス={0} 画像サイズ(高さ,幅)=({1},{2}) 抽出対象画像サイズ(高さ,幅)=({3},{4})");

    /**
     * リストアップを行う。
     *
     * @return 対象のサイズの画像ファイル一覧
     */
    public Set<File> makeList() {
        Set<File> set_output = Collections.synchronizedSet(new HashSet<File>());
        for (ImageSize size : this.getSizes()) {
            for (File F : this.ImageList) {
                StringBuilder sb = new StringBuilder();
                Object[] parameters;
                parameters = new Object[]{F.getAbsolutePath(), "UNKNOWN", "UNKNOWN", size.getHeight(), size.getWidth()};

                if (set_output.contains(F)) {
                    sb.append("別の条件をすでに満たしているファイル。");
                    sb.append(info.format(parameters));
                } else {
                    try {
                        BufferedImage Img = ImageIO.read(F);
                        parameters = new Object[]{F.getAbsolutePath(), Img.getHeight(), Img.getWidth(), size.getHeight(), size.getWidth()};

                        if (Img.getHeight() == size.getHeight() && Img.getWidth() == size.getWidth()) {
                            sb.append("条件を満たすピクセルのファイルを発見。");
                            sb.append(info.format(parameters));
                            set_output.add(F);
                        } else {
                            sb.append("条件を満たすピクセルのファイルではない。");
                            sb.append(info.format(parameters));
                        }
                    } catch (IOException e) {
                        //エラーが起きた場合、そのときのファイル名を出力する。
                        log.fatal("ファイル検索中にエラー ファイル = " + F.toString(), e);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        log.warn("壊れたファイルを読み込んだ可能性があります。 ファイル = " + F.toString());
                    }
                }

                if (log.isInfoEnabled()) {
                    log.info(sb.toString());
                }
                sb = null;
            }
        }
        return Collections.unmodifiableSet(set_output);
    }
}
