(ns image-thumbnail.core
  (:gen-class)
  (import [net.coobird.thumbnailator Thumbnails]))


(defn -main []
  (let [image (Thumbnails/fromFilenames (java.util.ArrayList. ["Hot_Dog_Dog.jpg"]))

        ; will create a thumbnail 160x160
        thumb (.forceSize image 160 160)

        ; will preserve aspect ratio and create thumb 160x144
        ;thumb (.size image 160 144)
        ]
    (.toFile thumb "thumbnail.jpg")))

