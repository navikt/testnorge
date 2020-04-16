export const getAllDatesBetween = (startdato, sluttdato) =>{
	const startDate = new Date(startdato)
	const endDate = new Date(sluttdato)

	let arr = []
	for(let dt=new Date(startDate.toDateString()); dt<=endDate; dt.setDate(dt.getDate()+1)){
		arr.push(new Date(dt.toDateString()));
	}
	return arr
}

