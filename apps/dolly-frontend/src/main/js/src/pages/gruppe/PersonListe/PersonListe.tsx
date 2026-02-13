import React, { Suspense, useEffect, useMemo, useState } from 'react'
import 'rc-tooltip/assets/bootstrap.css'
import { DollyTable } from '@/components/ui/dollyTable/DollyTable'
import Loading from '@/components/ui/loading/Loading'
import ContentContainer from '@/components/ui/contentContainer/ContentContainer'
import { ManIconItem, UnknownIconItem, WomanIconItem } from '@/components/ui/icon/IconItem'
import Icon from '@/components/ui/icon/Icon'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import useBoolean from '@/utils/hooks/useBoolean'
import { KommentarModal } from '@/pages/gruppe/PersonListe/modal/KommentarModal'
import { selectPersonListe, sokSelector } from '@/ducks/fagsystem'
import * as _ from 'lodash-es'
import DollyTooltip from '@/components/ui/button/DollyTooltip'
import { setSorting } from '@/ducks/finnPerson'
import { useDispatch } from 'react-redux'
import { TestComponentSelectors } from '#/mocks/Selectors'
import PersonVisningConnector from '@/pages/gruppe/PersonVisning/PersonVisningConnector'
import { DollyCopyButton } from '@/components/ui/button/CopyButton/DollyCopyButton'
import { Bestilling, useGruppeById } from '@/utils/hooks/useGruppe'
import { useLocation } from 'react-router'

const PersonIBrukButtonConnector = React.lazy(
	() => import('@/components/ui/button/PersonIBrukButton/PersonIBrukButtonConnector'),
)

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle',
}

export default function PersonListe({
	isFetching,
	search,
	gruppeId,
	identer,
	sidetall,
	sideStoerrelse,
	fagsystem,
	brukertype,
	visPerson,
	hovedperson,
	iLaastGruppe,
	fetchPdlPersoner,
	tmpPersoner,
	sorting,
}: any) {
	const [isKommentarModalOpen, openKommentarModal, closeKommentarModal] = useBoolean(false)
	const [selectedIdent, setSelectedIdent] = useState(null)
	const [identListe, setIdentListe] = useState([])
	const dispatch = useDispatch()
	const { gruppe: gruppeInfo } = useGruppeById(gruppeId)

	const bestillingStatuser = useMemo(() => {
		if (!gruppeInfo?.identer) {
			return undefined
		}
		return gruppeInfo.identer
			.flatMap((ident) => ident.bestillinger ?? [])
			.filter((bestilling) => bestilling?.id !== undefined)
			.sort((a, b) => (a.id < b.id ? 1 : -1))
			.reduce<Record<number, Bestilling>>((acc, bestilling) => {
				acc[bestilling.id] = bestilling
				return acc
			}, {})
	}, [gruppeInfo?.identer])

	const location = useLocation()

	const personListe = sokSelector(selectPersonListe(identer, bestillingStatuser, fagsystem), search)

	useEffect(() => {
		const idents =
			identer &&
			Object.values(identer).map((ident) => {
				if (ident) {
					return { ident: ident.ident, master: ident.master }
				}
			})
		if (!_.isEqual(idents, identListe)) {
			setIdentListe(idents)
		}
	}, [identer])

	useEffect(() => {
		if (_.isEmpty(identListe)) {
			return
		}
		fetchPdlPersoner(identListe)
	}, [identListe])

	const getKommentarTekst = (tekst) => {
		const beskrivelse = tekst.length > 170 ? tekst.substring(0, 170) + '...' : tekst
		return (
			<div style={{ maxWidth: 170 }}>
				<p>{beskrivelse}</p>
			</div>
		)
	}

	const getNavnLimited = (tekst) => {
		const navn = tekst.length > 20 ? tekst.substring(0, 20) + '...' : tekst
		return (
			<div style={{ maxWidth: '170px' }}>
				<p>{navn}</p>
			</div>
		)
	}

	const columnsDefault = [
		{
			text: 'Ident',
			width: '25',
			dataField: 'identNr',
			unique: true,

			formatter: (_cell, row) => (
				<DollyCopyButton
					displayText={row.identNr}
					copyText={row.identNr}
					tooltipText={'Kopier fødselsnummer'}
				/>
			),
		},
		{
			text: 'Navn',
			width: '40',
			dataField: 'navn',
			formatter: (_cell, row) => {
				return (
					<DollyTooltip content={row.navn?.length > 23 ? row.navn : null}>
						{getNavnLimited(row.navn)}
					</DollyTooltip>
				)
			},
		},
		{
			text: 'Alder',
			width: '15',
			dataField: 'alder',
		},
		{
			text: 'Bestilling-ID',
			width: '20',
			dataField: 'bestillingId',
			formatter: (_cell, row) => {
				const arr = row.bestillingId
				let str = arr[0]
				if (arr.length > 1) str = `${str} ...`
				return <>{str}</>
			},
		},
		{
			text: 'Status',
			width: '10',
			dataField: 'status',

			formatter: (cell) => (
				<Icon
					data-testid={TestComponentSelectors.BUTTON_OPEN_IDENT}
					kind={ikonTypeMap[cell]}
					title={cell}
				/>
			),
		},
		{
			text: 'Kilde',
			width: '20',
			dataField: 'kilde',
			sortField: 'master',
			headerCssClass: 'header-sort-sortable',
		},
		{
			text: 'Brukt',
			width: '15',
			style: { paddingLeft: '3px' },
			dataField: 'ibruk',
			sortField: 'iBruk',
			headerCssClass: 'header-sort-sortable',
			formatter: (_cell, row) => (
				<Suspense fallback={<Loading label={'Laster...'} />}>
					<PersonIBrukButtonConnector ident={row.ident} />
				</Suspense>
			),
		},
		{
			text: 'Notat',
			width: '10',
			dataField: 'harBeskrivelse',
			centerItem: true,
			formatter: (_cell, row) => {
				if (row.ident.beskrivelse) {
					return (
						<DollyTooltip
							content={getKommentarTekst(row.ident.beskrivelse)}
							align={{
								offset: [0, -10],
							}}
						>
							<div>
								<Icon
									kind="kommentar"
									size={20}
									onClick={(event) => {
										setSelectedIdent(row.ident)
										openKommentarModal()
										event.stopPropagation()
									}}
								/>
							</div>
						</DollyTooltip>
					)
				}
			},
		},
	]

	const columns = columnsDefault.map((column) => {
		const sortKolonne = sorting?.kolonne
		if (column.sortField && column.sortField === sortKolonne) {
			column.headerCssClass = sorting.retning === 'asc' ? 'header-sort-asc' : 'header-sort-desc'
		}
		return column
	})

	if (isFetching && personListe?.length === 0) {
		return <Loading label="Laster personer..." panel />
	}

	if (_.isEmpty(identer)) {
		const infoTekst =
			brukertype === 'BANKID'
				? 'Trykk på "Importer personer"-knappen for å kunne søke opp og importere identer til gruppen.'
				: 'Trykk på "Opprett personer"-knappen for å starte en bestilling eller "Importer personer"-knappen å kunne ' +
					'søke opp og importere identer til gruppen.'
		return <ContentContainer>{infoTekst}</ContentContainer>
	}

	const updatePersonHeader = () => {
		personListe.map((person) => {
			const redigertPerson = _.get(tmpPersoner?.pdlforvalter, `${person?.identNr}.person`)
			const fornavn = redigertPerson?.navn?.[0]?.fornavn || ''
			const mellomnavn = redigertPerson?.navn?.[0]?.mellomnavn
				? `${redigertPerson?.navn?.[0]?.mellomnavn?.charAt(0)}.`
				: ''
			const etternavn = redigertPerson?.navn?.[0]?.etternavn || ''

			if (redigertPerson) {
				if (!redigertPerson.doedsfall) {
					person.alder = person.alder.split(' ')[0]
				}
				person.kjonn = redigertPerson.kjoenn?.[0]?.kjoenn
				person.navn = `${fornavn} ${mellomnavn} ${etternavn}`
			}
		})
	}

	if (tmpPersoner) updatePersonHeader()

	const onHeaderClick = (value) => {
		const activeColumn = columns.filter(
			(column) => column.headerCssClass !== undefined && column.text === value,
		)

		if (!activeColumn || !activeColumn.length) {
			return
		}

		const sort_asc = 'header-sort-asc'
		const sort_desc = 'header-sort-desc'
		const sort_default = 'header-sort-sortable'

		columns.forEach((column) => {
			if (column.headerCssClass !== undefined) {
				if (column.text === value) {
					if (column.headerCssClass && column.headerCssClass.includes(sort_asc)) {
						column.headerCssClass = sort_desc
						dispatch(setSorting({ kolonne: column.sortField, retning: 'desc' }))
					} else {
						column.headerCssClass = sort_asc
						dispatch(setSorting({ kolonne: column.sortField, retning: 'asc' }))
					}
				} else {
					column.headerCssClass = sort_default
				}
			}
			return column
		})
	}

	return (
		<ErrorBoundary>
			<span data-testid={TestComponentSelectors.CONTAINER_DOLLY_TABLE}>
				<DollyTable
					data={personListe}
					columns={columns}
					gruppeDetaljer={{
						antallElementer: gruppeInfo?.antallIdenter,
						pageSize: sideStoerrelse,
					}}
					pagination
					iconItem={(bruker) => {
						if (bruker.kjonn === 'MANN' || bruker.kjonn === 'GUTT') {
							return <ManIconItem />
						} else if (bruker.kjonn === 'KVINNE' || bruker.kjonn === 'JENTE') {
							return <WomanIconItem />
						} else {
							return <UnknownIconItem />
						}
					}}
					visSide={location?.state?.sidetall || sidetall}
					visPerson={location?.state?.visPerson || visPerson}
					hovedperson={location?.state?.hovedperson || hovedperson}
					onExpand={(bruker) => (
						<Suspense fallback={<Loading label={'Laster ident...'} />}>
							<PersonVisningConnector
								ident={bruker.ident}
								personId={bruker.identNr}
								bestillingIdListe={bruker.ident.bestillingId}
								iLaastGruppe={iLaastGruppe}
								brukertype={brukertype}
							/>
						</Suspense>
					)}
					onHeaderClick={onHeaderClick}
				/>
				{isKommentarModalOpen && selectedIdent && (
					<KommentarModal closeModal={closeKommentarModal} ident={selectedIdent} />
				)}
			</span>
		</ErrorBoundary>
	)
}
