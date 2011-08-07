Introduction
========

UCI Security is a derivative of RBAC that slightly rearranges the components
found in a traditional access control system.  It includes the familiar idea of
roles to the extent that they are used to control the access a particular user
has within the system, but also introduces a new way of thinking about how their
relationship to a particular situation is relevant.

This project provides a reasonable starting point for integrating lightweight
RBAC into a new or existing system and is intended to be flexible enough to
handle just about any domain model based access situation.  This project has
gone through several iterations to maximize its ability to represent complex
domain model security relationships succinctly and to be able to evaluate these
in a reasonably scalable and efficient way.

Core Concepts
========

The following definitions will help shed some light on how the core concepts of
UCI Security are put together: 

* `User` - an identifiable person or automated process interacting with the system (for example "bob" or a user id)
* `Context` - the collection of zero or more objects that serve as the target of a user's intent
* `Intent` - the action that is intended to be performed by a user (as in "save" or "eatBBQ")
* `Role` - simple string that represents a collective access policy (like "admin")

Given a User, their target Context, and their Intent within that Context, we can
use UCI to determine if this request should be granted permission to execute or
not.  So far UCI does not deviate from traditional RBAC systems, and it provides
the same benefits of using those systems over ACL lists.  However, the mechanism
for deciding if the given request should be executed or not begins to diverge
from how we normally think about the rules for making these decisions.  Instead
of directly assigning Intents (actions) to particular roles, we instead first
define a set of Access Rules.

An Access Rule defines what roles are allowed to perform a given Intent based
on the given Context. Thus, if a User holds at least one of the given roles
within the given Context, then allow execution of the particular Intent. The
following representation is useful for describing this idea:

* `Access Rule  = ([intent, contexts], [role1, role2, ...])`

An intent is further broken down in to a particular category and an action (both
simple strings), purely for easing management of a large number of Intents. We
would then have the following:

* `Intent       = (category, action)`

Role Mappings are a way of representing what roles a User has in a given
Context.  They can be thought of as a simple map keyed by the User, a type of
Context, and a specific Context id, as in:

* `Role Mapping = ([user, contextType, contextId], [role1, role2, ...])`

The roles returned by this key are what we use to match against when evaluating
the Access Rule for an Intent.


Role Mapping Queries
========
(TODO)

Access Rule Evaluation
========
(TODO)

Vetoing Roles
--------
(TODO)

Caching
========
(TODO)

Examples
========
(TODO)

