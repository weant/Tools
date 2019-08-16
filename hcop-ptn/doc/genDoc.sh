java -jar C:/data/adoc/swagger2markup-cli-1.3.3.jar convert -i ../swagger_models/hcop-ptn.yaml -f ./hcop-ptn -c ./config.properties
asciidoctor -a toc=left -a toc-title=目录 -a toclevels=3 ./hcop-ptn.adoc
