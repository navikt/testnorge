import React from 'react'
import { useSelector } from 'react-redux'
import '~/pages/gruppe/PersonDetaljer/PersonDetaljer.less' // flytte denne
import '~/components/personInfoBlock/personInfoBlock.less' //flytte denne

export default function TpsfVisning(data) {
	console.log('data :', data)
	const counter = useSelector(state => state.testbruker)
	console.log('counter :', counter)
	// ikke render i function component!!
	const personDetaljer = data.data[0]
	const bostedAdresse = data.data[2]
	return (
		<div>
			<div className="person-details">
				<h3 className="flexbox--align-center">{personDetaljer.header}</h3>
				<div className="static-value">
					<h4>{personDetaljer.data[0].label}</h4>
					<span>{personDetaljer.data[0].value}</span>
				</div>
				<div>
					<h4>{personDetaljer.data[1].label}</h4>
					<span>{personDetaljer.data[1].value}</span>
				</div>
				<div>
					<h4>{personDetaljer.data[2].label}</h4>
					<span>{personDetaljer.data[2].value}</span>
				</div>
				<div>
					<h4>{personDetaljer.data[3].label}</h4>
					<span>{personDetaljer.data[3].value}</span>
				</div>
			</div>

			<div className="person-details">
				<h3 className="flexbox--align-center">{bostedAdresse.header}</h3>
				<div>
					<h4>{bostedAdresse.data[0].label}</h4>
					<span>{bostedAdresse.data[0].value}</span>
				</div>
				<div>
					<h4>Boadresse</h4>
					<span>{`${bostedAdresse.data[1].value} ${bostedAdresse.data[2].value}\n`}</span>
					<span>{bostedAdresse.data[8].value} postnummer</span>
				</div>
			</div>
			<p>hallo tpsf</p>
		</div>
	)
}

// import React from 'react'
// import { useSelector } from 'react-redux'

// export const CounterComponent = () => {
// 	const counter = useSelector(state => state.counter)
// 	return <div>{counter}</div>
// }
