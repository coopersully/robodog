package me.coopersully.robodog.events;

import me.coopersully.robodog.Commons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class HearLizardReplyLizard extends ListenerAdapter {

    public static String[] lizardURLs = new String[] {
            "https://cdn.britannica.com/79/165579-138-27764E9E/Komodo-dragons-handful-Indonesia-Lesser-Sunda-Islands.jpg?w=800&h=450&c=crop",
            "https://images.unsplash.com/photo-1504450874802-0ba2bcd9b5ae?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8bGl6YXJkfGVufDB8fDB8fA%3D%3D&w=1000&q=80",
            "https://i.pinimg.com/736x/fa/27/d7/fa27d7540cffbbe662cc69e6d7941650.jpg",
            "https://www.reactiongifs.com/r/hue.gif",
            "https://i.kym-cdn.com/photos/images/facebook/000/797/809/7a6.png",
            "https://c.tenor.com/TJP9khZspdcAAAAC/lizard-happy.gif",
            "https://c.tenor.com/f9lfRdZHNlQAAAAM/mlem-lizard.gif",
            "https://c.tenor.com/XWlW6IT17icAAAAM/lizard-animal.gif",
            "https://i.dailymail.co.uk/i/pix/2017/05/24/13/40BE3FF900000578-4530946-image-a-3_1495630340200.jpg",
            "https://static.boredpanda.com/blog/wp-content/uploads/2017/05/cute-happy-gecko-with-toy-kohaku-1-591e9c32b76f1__700.jpg",
            "https://static.boredpanda.com/blog/wp-content/uploads/2017/05/cute-happy-gecko-with-toy-kohaku-2-591e9c350806f__700.jpg",
            "https://pic-bstarstatic.akamaized.net/ugc/83ad5802401e240d826394e1ecbf3b0884d47407.jpg@1200w_630h_1e_1c_1f.webp",
            "https://preview.redd.it/unpnr2nmna341.jpg?width=640&crop=smart&auto=webp&s=3cd5b27a8503e76421e7e1f4f5def7a278db8f04",
            "https://petspruce.com/wp-content/uploads/2020/05/leopard-gecko-smile1.jpg",
            "https://cdn.hswstatic.com/gif/gecko-1.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRtysRFzi6T19vbLTcJ3eiQpxtATrt21nYVjQ&usqp=CAU",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcScTgEEiERYmYOFFq6RgCpTaK7HiM5R1hC88qL5QfmdN-44E8qg0LUMYuSpw5_Hlm2tghg&usqp=CAU",
            "https://a-z-animals.com/media/2022/02/Basilisk-Lizard-header.jpg",
            "https://www.rd.com/wp-content/uploads/2019/07/shutterstock_47641369.jpg",
            "https://www.vetvoice.com.au/imagevault/publishedmedia/x17vmrwbl7hedcr953f7/Lizard_-_Posing.jpg",
            "https://static.educalingo.com/img/en/800/lizard.jpg",
            "https://www.researchgate.net/publication/343504533/figure/fig4/AS:962816924188675@1606564851537/The-forest-green-lizard-Calotes-calotes-is-large-among-the-lizard-species-measuring.jpg",
            "https://blog.mystart.com/wp-content/uploads/IN_Gecko_Lizard_00.jpeg",
            "https://images.takeshape.io/86ce9525-f5f2-4e97-81ba-54e8ce933da7/dev/a5ba9791-ccc0-4f73-b62a-bde06a31d194/frilled%20lizard%20closed_resized.jpg",
            "https://stemcell.keck.usc.edu/wp-content/uploads/2021/10/mourning-gecko-web.jpg",
            "https://reptilesmagazine.com/wp-content/uploads/2022/01/crested-gecko-droplets-Dan-Olsen.jpg",
            "https://earimediaprodweb.azurewebsites.net/Api/v1/Multimedia/e33e2d6f-3ff0-4d78-b5f6-5a0ea0de974d/Rendition/low-res/Content/Public",
            "https://cdn.theatlantic.com/thumbor/DPvoWxxAQiR8ID-wZscdfz5-sx4=/33x86:3466x2017/960x540/media/old_wire/img/upload/2013/04/02/LizardMan/original.jpg",
            "https://www.worldatlas.com/r/w1200/upload/a2/0d/6a/shutterstock-313644251.jpg"
    };

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (!event.isFromGuild()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw().strip().toLowerCase();

        if (!content.contains("lizard")) return;

        String url = lizardURLs[Commons.random.nextInt(0, lizardURLs.length)];

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setImage(url);

        message.replyEmbeds(embedBuilder.build()).queue();
    }

}
