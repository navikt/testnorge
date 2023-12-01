import ContentContainer from '@/components/ui/contentContainer/ContentContainer'
import { Alert } from '@navikt/ds-react'
import { usePdlfPersoner } from '@/utils/hooks/usePdlForvalter'
import { DollyTable } from '@/components/ui/dollyTable/DollyTable'
import { ManIconItem, UnknownIconItem, WomanIconItem } from '@/components/ui/icon/IconItem'
import React from 'react'
import Loading from '@/components/ui/loading/Loading'
import { DollyCopyButton } from '@/components/ui/button/CopyButton/DollyCopyButton'
import { getAlder } from '@/ducks/fagsystem'
import { formatAlder } from '@/utils/DataFormatter'
import { NavigerTilPerson } from '@/pages/dollySoek/NavigerTilPerson'
import { PersonVisning } from '@/pages/dollySoek/PersonVisning'

export const ResultatVisning = ({ resultat }) => {
	const identString = resultat?.identer?.join(',')
	const { personer, loading, error } = usePdlfPersoner(identString)

	if (!resultat) {
		return (
			<ContentContainer>
				<Alert variant="info" size="small" inline>
					Ingen søk er gjort
				</Alert>
			</ContentContainer>
		)
	}

	if (resultat?.error) {
		return (
			<ContentContainer>
				<Alert variant={'error'} size={'small'} inline>
					Feil: {resultat.error}
				</Alert>
			</ContentContainer>
		)
	}

	if (resultat?.totalHits < 1) {
		return (
			<ContentContainer>
				<Alert variant="warning" size="small" inline>
					Ingen treff
				</Alert>
			</ContentContainer>
		)
	}

	const columns = [
		{
			text: 'Ident',
			width: '20',
			formatter: (_cell: any, row: any) => {
				const ident = row.person?.ident
				return <DollyCopyButton displayText={ident} copyText={ident} tooltipText={'Kopier ident'} />
			},
		},
		{
			text: 'Navn',
			width: '35',
			formatter: (_cell: any, row: any) => {
				const navn = row.person?.navn?.[0]
				const mellomnavn = navn?.mellomnavn ? `${navn.mellomnavn.charAt(0)}.` : ''
				return <>{`${navn?.fornavn} ${mellomnavn} ${navn?.etternavn}`}</>
			},
		},
		{
			text: 'Kjønn',
			width: '10',
			formatter: (_cell: any, row: any) => {
				const kjoenn = row.person?.kjoenn?.[0]?.kjoenn
				if (kjoenn === 'MANN' || kjoenn === 'GUTT') {
					return <>Mann</>
				} else if (kjoenn === 'KVINNE' || kjoenn === 'JENTE') {
					return <>Kvinne</>
				} else {
					return <>Ukjent</>
				}
			},
		},
		{
			text: 'Alder',
			width: '10',
			formatter: (_cell: any, row: any) => {
				const alder = getAlder(
					row.person?.foedsel?.[0]?.foedselsdato,
					row.person?.doedsfall?.[0]?.doedsdato,
				)
				return <>{formatAlder(alder, row.person?.doedsfall?.[0]?.doedsdato)}</>
			},
		},
		{
			text: 'Gruppe',
			width: '20',
			formatter: (_cell: any, row: any) => {
				return <NavigerTilPerson ident={row.person?.ident} />
			},
		},
	]

	if (loading) {
		return <Loading label={'Laster personer...'} />
	}

	return (
		<DollyTable
			data={personer}
			columns={columns}
			iconItem={(person) => {
				const kjoenn = person.person?.kjoenn?.[0]?.kjoenn
				if (kjoenn === 'MANN' || kjoenn === 'GUTT') {
					return <ManIconItem />
				} else if (kjoenn === 'KVINNE' || kjoenn === 'JENTE') {
					return <WomanIconItem />
				} else {
					return <UnknownIconItem />
				}
			}}
			onExpand={(person) => {
				return <PersonVisning person={person} loading={loading} />
			}}
		/>
	)
}
