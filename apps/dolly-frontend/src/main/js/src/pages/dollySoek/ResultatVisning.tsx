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
import { usePdlPersonbolk } from '@/utils/hooks/usePdlPerson'
import { runningE2ETest } from '@/service/services/Request'
import * as _ from 'lodash-es'

export const ResultatVisning = ({ resultat, soekError }) => {
	console.log('resultat: ', resultat) //TODO - SLETT MEG
	const identString = resultat?.identer?.join(',')
	const { pdlfPersoner, loading: loadingPdlf, error: errorPdlf } = usePdlfPersoner(identString)
	const { pdlPersoner, loading: loadingPdl, error: errorPdl } = usePdlPersonbolk(identString)

	// const personer = resultat?.identer?.map((ident) => {
	// 	const pdlfPerson = pdlfPersoner?.find((p) => p.person?.ident === ident)
	// 	const pdlPerson = pdlPersoner?.hentPersonBolk?.find((p) => p.ident === ident)
	// 	return pdlfPerson || pdlPerson
	// })

	const personer = resultat?.personer

	if (_.isEmpty(resultat)) {
		return (
			<ContentContainer>
				<Alert variant="info" size="small" inline>
					Ingen søk er gjort
				</Alert>
			</ContentContainer>
		)
	}

	if (resultat?.error || (errorPdlf && errorPdl) || soekError) {
		return (
			<ContentContainer>
				<Alert variant={'error'} size={'small'} inline>
					Feil: {resultat.error || errorPdl?.message || errorPdlf?.message || soekError?.message}
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
				const ident = row.hentIdenter?.identer?.find(
					(i) => i.gruppe === 'FOLKEREGISTERIDENT' && !i.historisk,
				)?.ident
				return <DollyCopyButton displayText={ident} copyText={ident} tooltipText={'Kopier ident'} />
			},
		},
		{
			text: 'Navn',
			width: '35',
			formatter: (_cell: any, row: any) => {
				// const navn = row.person?.navn?.[0]
				const navn = row.hentPerson?.navn?.find((i) => !i.metatdata?.historisk)
				if (!navn) {
					return <>Ukjent</>
				}
				const fornavn = navn?.fornavn || ''
				const mellomnavn = navn?.mellomnavn ? `${navn.mellomnavn.charAt(0)}.` : ''
				const etternavn = navn?.etternavn || ''
				return <>{`${fornavn} ${mellomnavn} ${etternavn}`}</>
			},
		},
		{
			text: 'Kjønn',
			width: '10',
			formatter: (_cell: any, row: any) => {
				// const kjoenn = row.person?.kjoenn?.[0]?.kjoenn
				const kjoenn = row.hentPerson?.kjoenn?.find((i) => !i.metadata?.historisk)?.kjoenn
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
				const foedselsdato =
					row.hentPerson?.foedsel?.find((i) => !i.metadata?.historisk)?.foedselsdato ||
					row.hentPerson?.foedselsdato?.find((i) => !i.metadata?.historisk)?.foedselsdato
				const doedsdato = row.hentPerson?.doedsfall?.find((i) => !i.metadata?.historisk)?.doedsdato
				if (!foedselsdato) {
					return <>Ukjent</>
				}
				const alder = getAlder(foedselsdato, doedsdato)
				return <>{formatAlder(alder, doedsdato)}</>
			},
		},
		{
			text: 'Gruppe',
			width: '20',
			formatter: (_cell: any, row: any) => {
				// return <NavigerTilPerson ident={row.person?.ident || row.ident} />
				return (
					<NavigerTilPerson
						ident={
							row.hentIdenter?.identer?.find(
								(i) => i.gruppe === 'FOLKEREGISTERIDENT' && !i.historisk,
							)?.ident
						}
					/>
				)
			},
		},
	]

	if (loadingPdlf || loadingPdl) {
		return <Loading label={'Laster personer...'} />
	}

	return (
		<DollyTable
			data={runningE2ETest() ? pdlfPersoner : personer}
			columns={columns}
			iconItem={(person) => {
				// console.log('person: ', person) //TODO - SLETT MEG
				// const kjoenn = person.person?.kjoenn?.[0]?.kjoenn
				const kjoenn = person.hentPerson?.kjoenn?.find((i) => !i.metadata?.historisk)?.kjoenn
				if (kjoenn === 'MANN' || kjoenn === 'GUTT') {
					return <ManIconItem />
				} else if (kjoenn === 'KVINNE' || kjoenn === 'JENTE') {
					return <WomanIconItem />
				} else {
					return <UnknownIconItem />
				}
			}}
			onExpand={(person) => {
				return <PersonVisning person={person} loading={loadingPdlf || loadingPdl} />
			}}
		/>
	)
}
