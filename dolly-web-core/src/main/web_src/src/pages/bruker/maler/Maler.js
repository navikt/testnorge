import React, { useState, useEffect } from 'react'
import config from '~/config'
import { api } from './api'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import Button from '~/components/ui/button/Button'
import { SlettButton } from '~/components/ui/button/SlettButton/SlettButton'
import { MalIconItem } from '~/components/ui/icon/IconItem'
import { EndreMalnavn } from './EndreMalnavn'

export default ({ brukerId }) => {
	const [loading, setLoading] = useState(true)
	const [maler, setMaler] = useState([])
	const [underRedigering, setUnderRedigering] = useState([])

	useEffect(() => {
		api
			.hentMaler()
			.then(data => {
				setMaler(data.malbestillinger[brukerId])
			})
			.then(() => setLoading(false))
	}, [])

	const erUnderRedigering = id => underRedigering.includes(id)

	const avbrytRedigering = id => {
		setUnderRedigering(underRedigering => underRedigering.filter(number => number !== id))
	}

	const columns = [
		{
			text: 'Malnavn',
			width: '80',
			dataField: 'malNavn',
			formatter: (cell, row) =>
				erUnderRedigering(row.id) ? (
					<EndreMalnavn malInfo={row} setMaler={setMaler} avbrytRedigering={avbrytRedigering} />
				) : (
					row.malNavn
				)
		},
		{
			text: 'Rediger malnavn',
			width: '10',
			formatter: (cell, row) => {
				return erUnderRedigering(row.id) ? (
					<Button onClick={() => avbrytRedigering(row.id)}>AVBRYT</Button>
				) : (
					<Button kind="edit" onClick={() => setUnderRedigering(underRedigering.concat([row.id]))}>
						ENDRE
					</Button>
				)
			}
		},
		{
			text: 'Slett',
			width: '10',
			dataField: 'status',
			formatter: (cell, row) => (
				<SlettButton action={() => slettMal(row.id, setMaler)} loading={loading}>
					Er du sikker på at du vil slette denne malen?
				</SlettButton>
			)
		}
	]

	if (loading) return <Loading label="Loading" />
	return (
		<>
			<h1>Dine maler</h1>
			<DollyTable
				data={maler}
				columns={columns}
				header={false}
				iconItem={<MalIconItem />}
				pagination
			/>
		</>
	)
}

const slettMal = (malId, setMaler) => {
	return api.slettMal(malId).then(() => {
		setMaler(maler => maler.filter(mal => mal.id !== malId))
	})
}
