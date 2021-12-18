# Steam Achievement Remote Unlock Mojo And eNforcer

*This project was created in honor of the Electronic Arts game support being unable to deal with achievement issues in the Steam client.*

## What is this library about?

Players love [Steam](https://store.steampowered.com/) achievements as a form of gratification for their accomplishments in a game. And it would really suck to not get some achievements simply because their unlock conditions are fragile (meaning you accomplish the objective over and over but some underlying hidden condition prevents the unlock), right?

Maybe the support can help? Oh, Steam support doesn't want to unlock random achievements they have no insight of? Oh, the game developer can't unlock achievements because they would have to change the implementation (and the player would still have to do the objective again after that) and simply don't care?

Well, why not implement a mechanism to allow a developer to hand out personalized (account-bound) or generalized (valid for all players) tokens to unlock specific achievements without creating a patch? **Here it is: Saruman!** Sadly, if you read this as a player, there is nothing it can do for you - this library is aimed at developers who need to include it into their game first (and already use [steamworks4j](https://github.com/code-disaster/steamworks4j), the dependency it's build on).

## How does it work?

The library consists of multiple maven artifacts working together:
1. **keygen**: A maven plugin generating asymmetric key pair files to include in your game (public key) and to store for yourself (private key).
2. **server**: A dependency for your support tool. Uses the private key, the achievement name and optionally a SteamID to generate a personalized or generalized unlock token for the given achievement.
3. **client**: Include this dependency in the packaged game and call it's functionality from your custom user interface where players should enter the unlock token.
4. **test**: For demonstration purposes only. Shows how to include the dependencies/plugin and how to call the functions.

**Use Case**: A player notices a missing achievement altough they accomplished the objective. They contact the developer/game support with the requested achievement name and their SteamID. The developer/support staff generates the unlock token and hands it out to the player. The player can enter the token (e.g. in a menu form) to trigger a call to [steamworks4j](https://github.com/code-disaster/steamworks4j) to unlock the achievement.

Generalized unlock tokens offer another way to prevent an overload of personalized token requests. They can be shared over the Steam Community and work for any player. However once a generalized unlock token is public, it can only be invalidated by creating new key files and patching the public key file in the packaged game.

---

Run `git config --add include.path ../.gitconfig` to include the template config in your project config.
