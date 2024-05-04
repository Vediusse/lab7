package viancis.lab6.server.commands;

import viancis.lab6.common.commands.CommandType;
import viancis.lab6.common.communication.Request;
import viancis.lab6.common.communication.Response;
import viancis.lab6.common.models.MusicBand;
import viancis.lab6.common.models.User;
import viancis.lab6.server.collection.Collection;

import java.util.HashMap;
import java.util.PriorityQueue;

public class RemoveGreaterCommand extends AbstractCommand {
    public RemoveGreaterCommand() {
        super("remove_greater", "удаляет из коллекции все элементы, превышающие заданный",1, CommandType.WITH_ARGUMENTS);
    }


    @Override
    public Response execute(Request request, Collection collection, User user) {
        if (request.commandArgs().length == args) {
            if(user == null){
                return new Response("Ошибка авторизации");
            }
            int numberOfParticipants;
            try {
                numberOfParticipants = Integer.parseInt(request.commandArgs()[0]);
            } catch (NumberFormatException e) {
                return new Response(false, "Arg is not a number");
            }
            return new Response(collection.removeGreater(numberOfParticipants,user));
        }
        return new Response(false,"Required " + args + " position argument");
    }
}
