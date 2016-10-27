package ru.lliepmah.sample.data;

import java.util.ArrayList;
import java.util.List;

import ru.lliepmah.sample.model.Person;
import ru.lliepmah.sample.model.Post;
import ru.lliepmah.sample.model.Screen;
import ru.lliepmah.sample.view.PostsActivity;
import ru.lliepmah.sample.view.PostsUsersActivity;
import ru.lliepmah.sample.view.UsersActivity;

/**
 * Created by Arthur Korchagin on 27.10.16
 */

public class DataSource {

    private static final List<Post> POSTS = new ArrayList<Post>() {{
        add(new Post(0, "Lorem ipsum dolor sit amet", "consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."));
        add(new Post(1, "Sed ut perspiciatis unde omnis", "Iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem."));
        add(new Post(2, "Neque porro quisquam est", "Qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam"));
        add(new Post(3, "Quis autem vel eum iure", "Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?"));
        add(new Post(4, "At vero eos et accusamus", "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis."));
        add(new Post(5, "On the other hand", "On the other hand, we denounce with righteous indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the moment"));
        add(new Post(6, "Quia voluptas", "Quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt."));
        add(new Post(7, "Praesentium voluptatum deleniti", "Praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident"));
        add(new Post(8, "Quis nostrum ", "Quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur?"));
        add(new Post(9, "Duis aute irure", "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."));
        add(new Post(10, "In turpis", "Aenean posuere, tortor sed cursus feugiat, nunc augue blandit nunc"));
    }};

    private static final List<Post> BEST_POSTS = new ArrayList<Post>() {{
        add(new Post(11, "Pellentesque posuere", "acus. Donec elit libero, sodales nec, volutpat a, suscipit non, turpis. Nullam sagittis. Suspendisse pulvinar, augue ac venenatis condimentum, sem libero volutpat nibh, nec pellentesque velit pede quis nunc"));
        add(new Post(12, "Praesent turpis", "Phasellus dolor. Maecenas vestibulum mollis.  Suspendisse pulvinar, augue ac venenatis condimentum"));
    }};

    private static final List<Person> USERS = new ArrayList<Person>() {{
        add(new Person(0, "Michael", "Corleone", "1-888-254-1584"));
        add(new Person(1, "Hyman", "Roth", "1-888-658-5484"));
        add(new Person(2, "Tony", "Montana", "1-888-548-4545"));
        add(new Person(3, "Manny", "Ribera", "1-888-456-5998"));
        add(new Person(4, "Rocco", "Lampone", "1-888-489-8447"));
        add(new Person(5, "James", "Conway", "1-888-899-8847"));
        add(new Person(6, "Tom", "Hagen", "1-987-988-8956"));
        add(new Person(7, "Henry", "Hill", "1-659-959-8959"));
    }};

    private static final List<Screen> SCREENS = new ArrayList<Screen>() {{
        add(new Screen("Posts", PostsActivity.class));
        add(new Screen("Users", UsersActivity.class));
        add(new Screen("Posts and Users", PostsUsersActivity.class));
    }};

    public static List<Post> getPosts() {
        return POSTS;
    }

    public static List<Post> getBestPosts() {
        return BEST_POSTS;
    }

    public static List<Person> getUsers() {
        return USERS;
    }

    public static List<Screen> getScreens() {
        return SCREENS;
    }

}
