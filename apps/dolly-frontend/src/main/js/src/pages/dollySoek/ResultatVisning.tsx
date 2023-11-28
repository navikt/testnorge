import ContentContainer from '@/components/ui/contentContainer/ContentContainer'
import { Alert } from '@navikt/ds-react'
import { usePdlfPersoner } from '@/utils/hooks/usePdlForvalter'
import { DollyTable } from '@/components/ui/dollyTable/DollyTable'
import { ManIconItem, UnknownIconItem, WomanIconItem } from '@/components/ui/icon/IconItem'
import React, { Suspense } from 'react'
import Loading from '@/components/ui/loading/Loading'
import { DollyCopyButton } from '@/components/ui/button/CopyButton/DollyCopyButton'
import { getAlder } from '@/ducks/fagsystem'
import { formatAlder } from '@/utils/DataFormatter'
import PersonVisningConnector from '@/pages/gruppe/PersonVisning/PersonVisningConnector'
import PdlfVisningConnector from '@/components/fagsystem/pdlf/visning/PdlfVisningConnector'
import { NavigerTilPerson } from '@/pages/dollySoek/NavigerTilPerson'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { usePdlMiljoeinfo, usePdlPersonbolk } from '@/utils/hooks/usePdlPerson'
import { PdlVisning } from '@/components/fagsystem/pdl/visning/PdlVisning'
import { useBestillingerPaaIdent } from '@/utils/hooks/usePersonSoek'
import { BestillingSammendragModal } from '@/components/bestilling/sammendrag/BestillingSammendragModal'
import { BestillingVisningListe } from '@/pages/dollySoek/BestillingVisningModal'

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

	// const { personer: test } = usePdlfPersoner(identString)
	// const { pdlData: personer, loading, error } = usePdlMiljoeinfo(identString)
	// const { pdlData, loading, error } = usePdlPersonbolk(identString)
	// console.log('test: ', test) //TODO - SLETT MEG
	// console.log('personer: ', personer) //TODO - SLETT MEG

	const columns = [
		{
			text: 'Ident',
			width: '20',
			formatter: (_cell: any, row: any) => {
				const ident = row.person?.ident
				// const ident = row.ident
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
				return (
					// <Suspense fallback={<Loading label={'Laster gruppe...'} />}>
					<NavigerTilPerson ident={row.person?.ident} />
					// <NavigerTilPerson ident={row.ident} />
					// </Suspense>
				)
			},
		},
	]

	if (loading) {
		return <Loading label={'Laster personer...'} />
	}

	return (
		<DollyTable
			data={personer}
			// data={pdlData?.hentPersonBolk}
			columns={columns}
			iconItem={(person) => {
				// console.log('person: ', person) //TODO - SLETT MEG
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
				// console.log('person: ', person) //TODO - SLETT MEG
				const { bestillinger } = useBestillingerPaaIdent(person.person?.ident)
				return (
					<>
						<StyledAlert variant={'info'} size={'small'} style={{ marginTop: '10px' }}>
							Søket er gjort mot bestillinger foretatt i Dolly, og denne personenen ble returnert
							fordi én eller flere av bestillingene samsvarer med søket. Fordi det er mulig å gjøre
							endringer på personer i etterkant av bestilling er det ikke sikkert at alle treffene
							stemmer overens med søket. Se bestillingen(e) knyttet til personen her:
							<BestillingVisningListe bestillinger={bestillinger?.data} />
						</StyledAlert>
						{/*//TODO Bestillingsknapp her?*/}
						{/*<BestillingSammendragModal bestilling={bestillinger?.data?.[0]} />*/}
						<PdlfVisningConnector fagsystemData={{ pdlforvalter: person }} loading={loading} />
						{/*<PdlVisning pdlData={{ hentPerson: person }} loading={loading} />*/}
						<StyledAlert variant={'info'} size={'small'} inline style={{ marginTop: '10px' }}>
							Viser kun egenskaper fra PDL,{' '}
							<NavigerTilPerson ident={person.person.ident} linkTekst={'vis person i gruppe'} /> for
							å se egenskaper fra alle fagsystemer.
						</StyledAlert>
					</>
				)
			}}
		/>
	)
}
