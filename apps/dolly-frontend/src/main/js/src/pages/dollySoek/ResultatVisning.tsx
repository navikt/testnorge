import ContentContainer from '@/components/ui/contentContainer/ContentContainer'
import { Alert, Pagination } from '@navikt/ds-react'
import { DollyTable } from '@/components/ui/dollyTable/DollyTable'
import { ManIconItem, UnknownIconItem, WomanIconItem } from '@/components/ui/icon/IconItem'
import React, { Fragment } from 'react'
import Loading from '@/components/ui/loading/Loading'
import { DollyCopyButton } from '@/components/ui/button/CopyButton/DollyCopyButton'
import { getAlder } from '@/ducks/fagsystem'
import { formatAlder } from '@/utils/DataFormatter'
import { NavigerTilPerson } from '@/pages/dollySoek/NavigerTilPerson'
import { PersonVisning } from '@/pages/dollySoek/PersonVisning'
import ItemCountSelect from '@/components/ui/dollyTable/pagination/ItemCountSelect/ItemCountSelect'

export const ResultatVisning = ({
	resultat,
	loading,
	soekError,
	visAntall,
	handleChangeSide,
	handleChangeAntall,
}) => {
	const startIndex = resultat?.side * visAntall + 1
	const lastIndex = resultat?.side * visAntall + resultat?.antall

	const personer = resultat?.personer

	if (loading) {
		return (
			<ContentContainer>
				<Loading label={'Laster personer ...'} />
			</ContentContainer>
		)
	}

	if (resultat?.error || soekError) {
		return (
			<ContentContainer>
				<Alert variant={'error'} size={'small'} inline>
					Feil: {resultat?.error || soekError?.message}
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
					(i) => i.gruppe !== 'AKTORID' && !i.historisk,
				)?.ident
				return <DollyCopyButton displayText={ident} copyText={ident} tooltipText={'Kopier ident'} />
			},
		},
		{
			text: 'Navn',
			width: '35',
			formatter: (_cell: any, row: any) => {
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
				return (
					<NavigerTilPerson
						ident={
							row.hentIdenter?.identer?.find((i) => i.gruppe !== 'AKTORID' && !i.historisk)?.ident
						}
					/>
				)
			},
		},
	]

	return (
		<>
			<DollyTable
				data={personer}
				columns={columns}
				iconItem={(person) => {
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
					return <PersonVisning person={person} loading={loading} />
				}}
			/>
			<div className="pagination-wrapper">
				<ItemCountSelect value={visAntall} onChangeHandler={handleChangeAntall} />
				{resultat?.totalHits > resultat?.antall && (
					<Fragment>
						<span className="pagination-label">
							Viser {startIndex}-{lastIndex} av {resultat?.totalHits}
						</span>
						<Pagination
							style={{ marginTop: '5px' }}
							page={resultat?.side + 1}
							count={Math.ceil(resultat?.totalHits / visAntall)}
							size={'xsmall'}
							onPageChange={handleChangeSide}
						/>
					</Fragment>
				)}
			</div>
		</>
	)
}
