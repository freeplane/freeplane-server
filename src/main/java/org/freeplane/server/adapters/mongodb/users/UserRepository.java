package org.freeplane.server.adapters.mongodb.users;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository  extends MongoRepository<User, String> {

	User findByUserName(String userName);
}
