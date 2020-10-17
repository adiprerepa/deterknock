package main

import (
	"net/http"
	"github.com/gorilla/mux"
)

func main() {
	r := mux.NewRouter()
	r.HandleFunc("/rooms/register", RegisterRoom)
	r.HandleFunc("/rooms/toggle", ToggleRoomState)
	http.Handle("/", r)
}

func RegisterRoom(w http.ResponseWriter, r *http.Request) {

}

func ToggleRoomState(w http.ResponseWriter, r *http.Request) {
	
}

