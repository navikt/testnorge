import React, { useState } from 'react'
import { DollyTable } from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import Button from '~/components/ui/button/Button'
import { SlettButton } from '~/components/ui/button/SlettButton/SlettButton'
import { MalIconItem } from '~/components/ui/icon/IconItem'
import { EndreMalnavn } from './EndreMalnavn'
import { slettMal } from './SlettMal'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { SearchField } from '~/components/searchField/SearchField'
import { Mal, useDollyMaler } from '~/utils/hooks/useMaler'
import { Alert } from '@navikt/ds-react'

export default ({ brukernavn }: { brukernavn: string }) => {
	const [searchText, setSearchText] = useState('')
	const [underRedigering, setUnderRedigering] = useState([])

	const { maler, loading } = useDollyMaler()

	if (loading) {
		return <Loading label="Loading" />
	}

	// @ts-ignore
	const egneMaler = maler[brukernavn] || []

	const erUnderRedigering = (id: string) => underRedigering.includes(id)

	const avbrytRedigering = (id: string) => {
		setUnderRedigering((erUnderRedigering) => erUnderRedigering.filter((number) => number !== id))
	}

	const columns = [
		{
			text: 'Malnavn',
			width: '80',
			dataField: 'malNavn',
			formatter: (_cell: any, row: { id: string; malNavn: string }) =>
				erUnderRedigering(row.id) ? (
					<EndreMalnavn malInfo={row} avbrytRedigering={avbrytRedigering} />
				) : (
					row.malNavn
				),
		},
		{
			text: 'Rediger malnavn',
			width: '13',
			formatter: (_cell: any, row: { id: string }) => {
				return erUnderRedigering(row.id) ? (
					<Button className="avbryt" onClick={() => avbrytRedigering(row.id)}>
						Avbryt
					</Button>
				) : (
					<Button kind="edit" onClick={() => setUnderRedigering(underRedigering.concat([row.id]))}>
						ENDRE NAVN
					</Button>
				)
			},
		},
		{
			text: 'Slett',
			width: '10',
			dataField: 'status',
			formatter: (_cell: any, row: { id: any }) => (
				<SlettButton action={() => slettMal(row.id, null)} loading={loading}>
					Er du sikker på at du vil slette denne malen?
				</SlettButton>
			),
		},
	]

	return (
		<div className="maloversikt">
			<hr />
			<div className="flexbox--space">
				<h2>Mine maler</h2>
				<SearchField placeholder={'Søk etter mal'} setText={setSearchText} />
			</div>
			{egneMaler.length > 0 ? (
				malerFiltrert(egneMaler, searchText).length > 0 ? (
					<ErrorBoundary>
						<DollyTable
							data={malerFiltrert(egneMaler, searchText)}
							columns={columns}
							header={false}
							iconItem={<MalIconItem />}
						/>
					</ErrorBoundary>
				) : (
					<Alert variant={'info'}>Ingen maler samsvarte med søket ditt</Alert>
				)
			) : (
				<Alert variant={'info'}>
					Du har ingen maler enda. Neste gang du oppretter en ny person kan du lagre bestillingen
					som en mal på siste side av bestillingsveilederen.
				</Alert>
			)}
		</div>
	)
}

const malerFiltrert = (malListe: Mal[], searchText: string) =>
	malListe.filter((mal) => mal.malNavn.toLowerCase().includes(searchText.toLowerCase()))
