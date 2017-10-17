# LizardIRC/FactorioRelay

This project is a simple bidirectional relay between IRC and a Factorio server which uses the Factorio server's
log and FIFO files for communication.

## Building FactorioRelay
To build FactorioRelay from source, you'll need the Java SE 8 JDK and Apache Maven (version 3.0.0 or later recommended).
You can also use a Maven-compatible IDE, such as IntelliJ IDEA or Eclipse, instead of Maven.

Simply clone the repository, run `mvn package`, and run the resulting jarfile!  The bot will automatically create a
default configuration file for you then terminate, so you have a chance to set things up; then just run the bot again!

## Setup and Usage
Setup and usage information will be added here in the future, after the bot has been written and seems reliable.

## Contributing
As an open source project, you are welcome to contribute to FactorioRelay's development!

### Bug reports/feature requests/other issues
If you would like to file a bug report, request a feature, or report some other issue, please use the [Issues section
of our GitHub repository][github-issues].

### Code
If you would like to contribute code, there are a couple different ways you could go about doing this.

* If you are familiar with Gerrit Code Review, you can use [LizardNet Code Review][lizardnet-code-review] to clone
  the code, and submit patches directly to us that way.  Note though that you'll need a LizardWiki account to log in to
  LizardNet Code Review; [this page][lizardnet-code-review-login] has more information on that (if you don't have a
  LizardWiki account, you can easily request one be created for you).
* Alternatively, just clone the [GitHub repository][github] and submit a pull request.  Note, though, that the GitHub
  repository is just a read-only mirror of the [LizardNet Code Review repository][lizardnet-repository], so all pull
  requests will be copied to Gerrit by a Beancounter developer for you before merging into the mainline.

## Licensing/Authors/Acknowledgments
LizardIRC/FactorioRelay is licensed under the GNU GPL v3+.  For more information, please see the LICENSE.txt file.  For
authors information, please see the AUTHORS.txt file.

This project contains code from and components derived from the Beancounter general-purpose IRC bot, also developed by
LizardIRC.  Please see [here][lizardirc-beancounter] for more information.  Beancounter is also licensed under the GNU
GPL v3+.

[lizardirc-beancounter]: https://www.lizardirc.org/index.php?page=beancounter
[github]: https://github.com/LizardNet/LizardIRC-FactorioRelay
[github-issues]: https://github.com/LizardNet/LizardIRC-FactorioRelay/issues
[lizardnet-code-review]: https://gerrit.fastlizard4.org
[lizardnet-repository]: https://git.fastlizard4.org/gitblit/summary/?r=LizardIRC/FactorioRelay.git
[lizardnet-code-review-login]: https://fastlizard4.org/wiki/LizardNet_Code_Review
