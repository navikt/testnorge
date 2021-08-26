import React, { useEffect, useState } from 'react'
import { api } from './api'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import Button from '~/components/ui/button/Button'
import { SlettButton } from '~/components/ui/button/SlettButton/SlettButton'
import { MalIconItem } from '~/components/ui/icon/IconItem'
import { EndreMalnavn } from './EndreMalnavn'
import { slettMal } from './SlettMal'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { SearchField } from '~/components/searchField/SearchField'

export default ({ brukerId }) => {
	const [loading, setLoading] = useState(true)
	const [maler, setMaler] = useState([])
	const [searchText, setSearchText] = useState('')
	const [underRedigering, setUnderRedigering] = useState([])

	useEffect(() => {
		api
			.hentMaler()
			.then(data => {
				setMaler(data.malbestillinger[`${brukerId}`] || [])
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
					<Button className="avbryt" onClick={() => avbrytRedigering(row.id)}>
						AVBRYT
					</Button>
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
		<div className="maloversikt">
			<hr />
			<div className="flexbox--space">
				<h2>Mine maler</h2>
				<SearchField placeholder={'Søk etter mal'} setText={setSearchText} />
			</div>
			{maler.length > 0 ? (
				malerFiltrert(maler, searchText).length > 0 ? (
					<ErrorBoundary>
						<DollyTable
							data={malerFiltrert(maler, searchText)}
							columns={columns}
							header={false}
							iconItem={<MalIconItem />}
							pagination
						/>
					</ErrorBoundary>
				) : (
					<AlertStripeInfo>Ingen maler samsvarte med søket ditt</AlertStripeInfo>
				)
			) : (
				<AlertStripeInfo>
					Du har ingen maler enda. Neste gang du oppretter en ny person kan du lagre bestillingen
					som en mal på siste side av bestillingsveilederen.
				</AlertStripeInfo>
			)}
		</div>
	)
}

const malerFiltrert = (maler, searchText) =>
	maler.filter(mal => mal.malNavn.toLowerCase().includes(searchText.toLowerCase()))
