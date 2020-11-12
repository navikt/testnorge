import React from 'react'

export const TpsDataVisning = ({ data }) => {
	console.log('data :>> ', data)

	return (
		<div className="flexbox--flex-wrap">
			{data.map((miljoe, idx) => {
				// console.log('miljoe :>> ', miljoe.environment)
				return <p key={idx}>{miljoe.environment}</p>
			})}
		</div>
	)
}
