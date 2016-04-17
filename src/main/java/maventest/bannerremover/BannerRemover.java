/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maventest.bannerremover;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import maventest.bannerremover.FileSeeker.PictureFileSeeker;
import maventest.bannerremover.config.ConfigLoader;
import maventest.bannerremover.sizechecker.SizeChecker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 画像ファイルの中から特定のサイズのものを探して除去する。
 */
public class BannerRemover {

    /**
     * @param args the command line arguments 検索対象の最上位ディレクトリ
     * サブディレクトリの探査を行うかを0か1で指定 画像の高さ 画像の幅の順で指定
     */
    public static void main(String[] args) {
        Log log = LogFactory.getLog(BannerRemover.class);


        ConfigLoader conf = new ConfigLoader(new File(args[0]));

        PictureFileSeeker seeker = new PictureFileSeeker(conf.getSourceDir());

        List<File> images = seeker.seek();

        SizeChecker checker = new SizeChecker(images);

        checker.setSizes(conf.getSizes());

        Set<File> banners = checker.makeList();

        for (File f : banners) {
            try {
                log.info("移動します。" + f.toString());
                FileUtils.moveFileToDirectory(f, conf.getDestDir(), true);
                log.info("移動しました。" + f.toString());
            } catch (IOException ex) {
                log.error(ex);
            }
        }
    }
}
