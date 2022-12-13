import React, { useEffect, useMemo, useState } from 'react'
import 'rc-tooltip/assets/bootstrap.css'
import { DollyTable } from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import PersonIBrukButtonConnector from '~/components/ui/button/PersonIBrukButton/PersonIBrukButtonConnector'
import PersonVisningConnector from '../PersonVisning/PersonVisningConnector'
import { ManIconItem, UnknownIconItem, WomanIconItem } from '~/components/ui/icon/IconItem'

import Icon from '~/components/ui/icon/Icon'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import useBoolean from '~/utils/hooks/useBoolean'
import { KommentarModal } from '~/pages/gruppe/PersonListe/modal/KommentarModal'
import { selectPersonListe, sokSelector } from '~/ducks/fagsystem'
import { isEmpty, isEqual } from 'lodash'
import { CopyButton } from '~/components/ui/button/CopyButton/CopyButton'
import _get from 'lodash/get'
import DollyTooltip from '~/components/ui/button/DollyTooltip'
import { setSorting } from '~/ducks/finnPerson'
import { useDispatch } from 'react-redux'
import { useBestillingerGruppe } from '~/utils/hooks/useBestilling'

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle',
}

export default function PersonListe({
	isFetching,
	search,
	gruppeInfo,
	identer,
	sidetall,
	sideStoerrelse,
	fagsystem,
	brukertype,
	visPerson,
	hovedperson,
	iLaastGruppe,
	fetchTpsfPersoner,
	fetchPdlPersoner,
	tmpPersoner,
	sorting,
	bestillingerById,
}) {
	const [isKommentarModalOpen, openKommentarModal, closeKommentarModal] = useBoolean(false)
	const [selectedIdent, setSelectedIdent] = useState(null)
	const [identListe, setIdentListe] = useState([])
	const dispatch = useDispatch()
	const { bestillingerById: bestillingStatuser } = useBestillingerGruppe(gruppeInfo.id)

	const personListe = useMemo(
		() => sokSelector(selectPersonListe(identer, bestillingStatuser, fagsystem), search),
		[identer, search, fagsystem, bestillingStatuser, visPerson]
	)

	useEffect(() => {
		const idents =
			identer &&
			Object.values(identer).map((ident) => {
				if (ident) {
					return { ident: ident.ident, master: ident.master }
				}
			})
		if (!isEqual(idents, identListe)) {
			setIdentListe(idents)
		}
	}, [identer])

	useEffect(() => {
		if (isEmpty(identListe)) {
			return
		}
		fetchTpsfPersoner(identListe)
		fetchPdlPersoner(identListe)
	}, [identListe, visPerson, bestillingerById])

	const getKommentarTekst = (tekst) => {
		const beskrivelse = tekst.length > 170 ? tekst.substring(0, 170) + '...' : tekst
		return (
			<div style={{ maxWidth: 200 }}>
				<p>{beskrivelse}</p>
			</div>
		)
	}

	const columnsDefault = [
		{
			text: 'Ident',
			width: '20',
			dataField: 'identNr',
			unique: true,

			formatter: (_cell, row) => <CopyButton value={row.identNr} />,
		},
		{
			text: 'Navn',
			width: '30',
			dataField: 'navn',
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

			formatter: (cell) => <Icon kind={ikonTypeMap[cell]} title={cell} />,
		},
		{
			text: 'Kilde',
			width: '15',
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
			formatter: (_cell, row) => <PersonIBrukButtonConnector ident={row.ident} />,
		},
		{
			text: '',
			width: '10',
			dataField: 'harBeskrivelse',
			centerItem: true,
			formatter: (_cell, row) => {
				if (row.ident.beskrivelse) {
					return (
						<DollyTooltip
							overlay={getKommentarTekst(row.ident.beskrivelse)}
							destroyTooltipOnHide={true}
							mouseEnterDelay={0}
							onClick={(event) => {
								setSelectedIdent(row.ident)
								openKommentarModal()
								event.stopPropagation()
							}}
							arrowContent={<div className="rc-tooltip-arrow-inner" />}
							align={{
								offset: ['0', '-10'],
							}}
						>
							<div style={{ textAlign: 'center' }}>
								<Icon kind="kommentar" size={20} />
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

	if (isFetching || (personListe?.length === 0 && !isEmpty(identer))) {
		return <Loading label="Laster personer" panel />
	}

	if (isEmpty(identer)) {
		const infoTekst =
			brukertype === 'BANKID'
				? 'Trykk på "Importer personer"-knappen for å kunne søke opp og importere identer til gruppen.'
				: 'Trykk på "Opprett personer"-knappen for å starte en bestilling eller "Importer personer"-knappen å kunne ' +
				  'søke opp og importere identer til gruppen.'
		return <ContentContainer>{infoTekst}</ContentContainer>
	}

	const updatePersonHeader = () => {
		personListe.map((person) => {
			const redigertPerson = _get(tmpPersoner?.pdlforvalter, `${person?.identNr}.person`)
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
			(column) => column.headerCssClass !== undefined && column.text === value
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
			<DollyTable
				data={personListe}
				columns={columns}
				gruppeDetaljer={{
					antallElementer: gruppeInfo.antallIdenter,
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
				visSide={sidetall}
				visPerson={visPerson}
				hovedperson={hovedperson}
				onExpand={(bruker) => (
					<PersonVisningConnector
						ident={bruker.ident}
						personId={bruker.identNr}
						bestillingIdListe={bruker.ident.bestillingId}
						iLaastGruppe={iLaastGruppe}
						brukertype={brukertype}
					/>
				)}
				onHeaderClick={onHeaderClick}
			/>
			{isKommentarModalOpen && selectedIdent && (
				<KommentarModal closeModal={closeKommentarModal} ident={selectedIdent} />
			)}
		</ErrorBoundary>
	)
}
