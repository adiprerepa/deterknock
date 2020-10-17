package main

import (
	"fmt"
	"database/sql"
	"log"
	"os"
)

const (
	keyDBName = "ROOM_DB_NAME"
	keyDBUser = "ROOM_DB_USER"
 	keyDBPass = "ROOM_DB_PASS"
)

type RoomStore struct {
	database *sql.DB
	roomTableName string
}

type Room struct {
	Name string
	IP string
}

func NewRoomStore(endpoint string) *RoomStore {
	deps := GetEnvDeps()
	dnsStr := fmt.Sprintf("%s:%s@tcp(%s)/%s", deps.User, deps.Pass, endpoint, deps.Name)
	conn, err := sql.Open("mysql", dnsStr)
	if err != nil {
		log.Fatalf("[db] unable to open connection to db: %v", err)
		return nil
	}
	return &RoomStore{
		database: conn,
		roomTableName: "rooms",
	}
}

func (r *RoomStore) InsertRoom(room Room) error {
	query := fmt.Sprintf(`insert into %s (name, ip) values (?, ?)`, r.roomTableName)
	if _, err := r.database.Exec(query, room.Name, room.IP); err != nil {
		return err
	}
	return nil
}

func (r *RoomStore) GetRooms() ([]Room, error) {
	query := fmt.Sprintf(`select Name, IP from %s`, r.roomTableName)
	rowSet, err := r.database.Query(query)
	if err != nil {
		return nil, err
	}
	var rooms []Room
	defer rowSet.Close()
	for rowSet.Next() {
		var Name, IP string
		err = rowSet.Scan(&Name, &IP)
		if err != nil {
			return nil, err
		}
		rooms = append(rooms, Room{
			Name: Name,
			IP: IP,
		})
	}
	return rooms, nil
}

func (r *RoomStore) GetRoomIP(name string) (

type DbEnvironmentDeps struct {
	Name string
	User string
	Pass string
}

func GetEnvDeps() DbEnvironmentDeps {
	name := os.Getenv(keyDBUser)
	user := os.Getenv(keyDBUser)
	pass := os.Getenv(keyDBPass)
	if name == "" || user == "" || pass == "" {
		log.Fatal("[db] unset database name, username, or password")
	}
	return DbEnvironmentDeps{
		Name: name,
		User: user,
		Pass: pass,
	}
}

