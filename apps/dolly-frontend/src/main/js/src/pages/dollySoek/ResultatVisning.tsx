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
import _ from 'lodash'

export const ResultatVisning = ({ resultat, soekError }) => {
	const identString = resultat?.identer?.join(',')
	const { pdlfPersoner, loading: loadingPdlf, error: errorPdlf } = usePdlfPersoner(identString)
	const { pdlPersoner, loading: loadingPdl, error: errorPdl } = usePdlPersonbolk(identString)

	const personer = resultat?.identer?.map((ident) => {
		const pdlfPerson = pdlfPersoner?.find((p) => p.person?.ident === ident)
		const pdlPerson = pdlPersoner?.hentPersonBolk?.find((p) => p.ident === ident)
		return pdlfPerson || pdlPerson
	})

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
				const ident = row.person?.ident || row.ident
				return <DollyCopyButton displayText={ident} copyText={ident} tooltipText={'Kopier ident'} />
			},
		},
		{
			text: 'Navn',
			width: '35',
			formatter: (_cell: any, row: any) => {
				const navn = row.person?.navn?.[0]
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
				if (
					!row.person?.foedsel?.[0]?.foedselsdato &&
					!row.person?.foedselsdato?.[0]?.foedselsdato
				) {
					return <>Ukjent</>
				}
				const alder = getAlder(
					row.person?.foedselsdato?.[0]?.foedselsdato || row.person?.foedsel?.[0]?.foedselsdato,
					row.person?.doedsfall?.[0]?.doedsdato,
				)
				return <>{formatAlder(alder, row.person?.doedsfall?.[0]?.doedsdato)}</>
			},
		},
		{
			text: 'Gruppe',
			width: '20',
			formatter: (_cell: any, row: any) => {
				return <NavigerTilPerson ident={row.person?.ident || row.ident} />
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
				return <PersonVisning person={person} loading={loadingPdlf || loadingPdl} />
			}}
		/>
	)
}
