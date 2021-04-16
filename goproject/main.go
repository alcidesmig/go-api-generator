package main

import (
	"log"
	"io/ioutil"
	"net/http"
	"encoding/json"
	"errors"
	"strconv"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
	"github.com/husobee/vestigo"
)

var db, _ = gorm.Open(sqlite.Open("gorm.db"), &gorm.Config{})

func main() {
	db.AutoMigrate(&User{}, &Notebook{})
	router := vestigo.NewRouter()
	router.Post("/user", PostUser)
	router.Delete("/user/:id", DeleteUser)
	router.Get("/user/:id", GetUserByID)
	router.Get("/notebooks", GetNotebooks)
	router.Get("/user", GetUsers)
	router.Put("/user/:id", PutUser)
	log.Fatal(http.ListenAndServe(":8080", router))
}

type User struct {
	ID int `json:"id";gorm:"primaryKey"`
	Idade float64 `json:"Idade"`
	Nome string `json:"Nome"`
	NotebookID int `json:"NotebookID"`
	Notebook *Notebook `json:"Notebook,omitempty";gorm:"references:NotebookID"`
}

type Notebook struct {
	ID int `json:"id";gorm:"primaryKey"`
	Name string `json:"Name"`
}


func DatabaseGetUserByID(id int) (user *User, err error) {
	user = new(User)
	res := db.Where("id = ?", id).Find(&user)
	err = res.Error
	if errors.Is(err, gorm.ErrRecordNotFound) || res.RowsAffected == 0 {
		return nil, gorm.ErrRecordNotFound
	}
	return
}

func DatabaseGetUsers() (users []*User, err error) {
	res := db.Model(&User{}).Find(&users)
	err = res.Error
	if errors.Is(err, gorm.ErrRecordNotFound) || res.RowsAffected == 0 {
		return nil, gorm.ErrRecordNotFound
	}
	return
}

func DatabaseGetNotebookByID(id int) (notebook *Notebook, err error) {
	notebook = new(Notebook)
	res := db.Where("id = ?", id).Find(&notebook)
	err = res.Error
	if errors.Is(err, gorm.ErrRecordNotFound) || res.RowsAffected == 0 {
		return nil, gorm.ErrRecordNotFound
	}
	return
}

func DatabaseGetNotebooks() (notebooks []*Notebook, err error) {
	res := db.Model(&Notebook{}).Find(&notebooks)
	err = res.Error
	if errors.Is(err, gorm.ErrRecordNotFound) || res.RowsAffected == 0 {
		return nil, gorm.ErrRecordNotFound
	}
	return
}


func (user User) Save() (err error) {
	res := db.Save(&user)
	err = res.Error
	return
}

func (notebook Notebook) Save() (err error) {
	res := db.Save(&notebook)
	err = res.Error
	return
}


func (user User) Delete() (err error) {
	res := db.Delete(&user)
	err = res.Error
	return
}

func (notebook Notebook) Delete() (err error) {
	res := db.Delete(&notebook)
	err = res.Error
	return
}

func PostUser(w http.ResponseWriter, r *http.Request) {
	var user User
	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Println("Error while parsing body")
		w.WriteHeader(400)
		w.Write([]byte(""))
		return
	}

	err = json.Unmarshal(body, &user)
	if err != nil {
		log.Println("Error while unmarshalling data")
		w.WriteHeader(400)
		w.Write([]byte(""))
		return
	}

	err = user.Save()
	if err != nil {
		log.Println("Error while saving to db")
		w.WriteHeader(500)
		w.Write([]byte(""))
		return
	}
	w.WriteHeader(200)
	w.Write([]byte(""))
	return
}

func DeleteUser(w http.ResponseWriter, r *http.Request) {
	strid := vestigo.Param(r, "id")
	id, _ := strconv.Atoi(strid)
	var user User
	user.ID = id
	err := user.Delete()
	if err != nil {
		log.Println("Error while deleting in db")
		w.WriteHeader(500)
		w.Write([]byte(""))
		return
	}
	w.WriteHeader(200)
	w.Write([]byte(""))
	return
}

func GetUserByID(w http.ResponseWriter, r *http.Request) {
	strid := vestigo.Param(r, "id")
	id, _ := strconv.Atoi(strid)
	response, err := DatabaseGetUserByID(id)
	w.Header().Set("Content-Type", "application/json")
	if err != nil && errors.Is(err, gorm.ErrRecordNotFound) {
		w.WriteHeader(404)
		w.Write([]byte("{}"))
		return
	} else if err != nil {
		w.WriteHeader(500)
		w.Write([]byte(""))
		return
	}
	jsonResponse, _ := json.Marshal(response)
	w.WriteHeader(200)
	w.Write(jsonResponse)
	return
}

func GetNotebooks(w http.ResponseWriter, r *http.Request) {
	response, err := DatabaseGetNotebooks()
	w.Header().Set("Content-Type", "application/json")
	if err != nil && errors.Is(err, gorm.ErrRecordNotFound) {
		w.WriteHeader(404)
		w.Write([]byte("{}"))
		return
	} else if err != nil {
		w.WriteHeader(500)
		w.Write([]byte(""))
		return
	}
	jsonResponse, _ := json.Marshal(response)
	w.WriteHeader(200)
	w.Write(jsonResponse)
	return
}

func GetUsers(w http.ResponseWriter, r *http.Request) {
	response, err := DatabaseGetUsers()
	w.Header().Set("Content-Type", "application/json")
	if err != nil && errors.Is(err, gorm.ErrRecordNotFound) {
		w.WriteHeader(404)
		w.Write([]byte("{}"))
		return
	} else if err != nil {
		w.WriteHeader(500)
		w.Write([]byte(""))
		return
	}
	jsonResponse, _ := json.Marshal(response)
	w.WriteHeader(200)
	w.Write(jsonResponse)
	return
}

func PutUser(w http.ResponseWriter, r *http.Request) {
	strid := vestigo.Param(r, "id")
	id, _ := strconv.Atoi(strid)
	response, err := DatabaseGetUserByID(id)
	w.Header().Set("Content-Type", "application/json")
	if err != nil && errors.Is(err, gorm.ErrRecordNotFound) {
		w.WriteHeader(404)
		w.Write([]byte("{}"))
		return
	} else if err != nil {
		w.WriteHeader(500)
		w.Write([]byte(""))
		return
	}
	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Println("Error while parsing body")
		w.WriteHeader(400)
		w.Write([]byte(""))
		return
	}
	err = json.Unmarshal(body, &response)
	if err != nil {
		log.Println("Error while unmarshalling data")
		w.WriteHeader(400)
		w.Write([]byte(""))
		return
	}
	err = response.Save()
	if err != nil {
		log.Println("Error while saving to db")
		w.WriteHeader(500)
		w.Write([]byte(""))
		return
	}
	w.WriteHeader(200)
	w.Write([]byte("\"{\"message\":\"updated\"}\""))
	return
}

