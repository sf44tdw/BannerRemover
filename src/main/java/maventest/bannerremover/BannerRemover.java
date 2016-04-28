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
 * 画像ファイルの中から特定のサイズのものを探して除去する。 設定ファイルはUTF-8 BOM無しの文字コードを使用する必要あり。
 */
public class BannerRemover {

    /**
     * @param args the command line arguments 設定ファイルのパス
     */
    public static void main(String[] args) {
        Log log = LogFactory.getLog(BannerRemover.class);

        ConfigLoader conf = null;
        try {
            conf = new ConfigLoader(new File(args[0]));
        } catch (Exception ex) {
            log.fatal(ex);
        }
        PictureFileSeeker seeker = new PictureFileSeeker(conf.getSourceDir());

        List<File> images = seeker.seek();
        log.info("画像ファイル探索完了。ファイル数=" + images.size());
        
        SizeChecker checker = new SizeChecker(images);
        checker.setSizes(conf.getSizes());
        
        Set<File> banners = checker.makeList();
        log.info("指定サイズのファイル数=" + images.size());
        
        for (File f : banners) {
            try {
                log.info("移動します。" + f.toString());
                FileUtils.moveFileToDirectory(f, conf.getDestDir(), true);
                log.info("移動しました。" + f.toString());
            } catch (IOException ex) {
                log.fatal(ex);
            }
        }
        log.info("正常終了。");
    }
}
