import React, { useEffect, useMemo, useState } from 'react'
import Tooltip from 'rc-tooltip'
import 'rc-tooltip/assets/bootstrap.css'
import { DollyTable } from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import PersonIBrukButtonConnector from '~/components/ui/button/PersonIBrukButton/PersonIBrukButtonConnector'
import PersonVisningConnector from '../PersonVisning/PersonVisningConnector'
import { ManIconItem, WomanIconItem } from '~/components/ui/icon/IconItem'

import Icon from '~/components/ui/icon/Icon'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import useBoolean from '~/utils/hooks/useBoolean'
import { KommentarModal } from '~/pages/gruppe/PersonListe/modal/KommentarModal'
import { selectPersonListe, sokSelector } from '~/ducks/fagsystem'
import { isEqual } from 'lodash'
import { CopyButton } from '~/components/ui/button/CopyButton/CopyButton'
import _get from 'lodash/get'

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle',
}

export default function PersonListe({
	isFetching,
	search,
	fagsystem,
	bestillingStatuser,
	gruppeInfo,
	identer,
	sidetall,
	slettedeIdenter,
	sideStoerrelse,
	brukertype,
	visPerson,
	iLaastGruppe,
	fetchTpsfPersoner,
	fetchPdlPersoner,
	fetchIdenterById,
	setVisning,
	tmpPersoner,
}) {
	const [isKommentarModalOpen, openKommentarModal, closeKommentarModal] = useBoolean(false)
	const [selectedIdent, setSelectedIdent] = useState(null)
	const [identListe, setIdentListe] = useState([])

	const personListe = useMemo(
		() => sokSelector(selectPersonListe(identer, bestillingStatuser, fagsystem), search),
		[identer, search, fagsystem, visPerson]
	)

	useEffect(() => {
		fetchIdenterById(gruppeInfo.id, sidetall, sideStoerrelse)
	}, [gruppeInfo.id, sidetall, sideStoerrelse])

	useEffect(() => {
		const idents = Object.values(identer)
			.filter((ident) => !slettedeIdenter?.[0]?.includes(ident.ident))
			.map((ident) => {
				if (ident) {
					return { ident: ident.ident, master: ident.master }
				}
			})
		if (!isEqual(idents, identListe)) {
			setIdentListe(idents)
		}
	}, [identer, slettedeIdenter[0]])

	useEffect(() => {
		fetchTpsfPersoner(identListe)
		fetchPdlPersoner(identListe)
	}, [identListe, sidetall, sideStoerrelse, visPerson])

	if (isFetching) return <Loading label="Laster personer" panel />

	if (visPerson && personListe && window.sessionStorage.getItem('sidetall')) {
		window.sessionStorage.removeItem('sidetall')
	}

	if (!personListe || personListe.length === 0) {
		const infoTekst =
			brukertype === 'BANKID'
				? 'Trykk på importer personer-knappen for å kunne søke opp og importere identer til gruppen.'
				: 'Trykk på opprett personer-knappen for å starte en bestilling.'
		return <ContentContainer>{infoTekst}</ContentContainer>
	}

	const getKommentarTekst = (tekst) => {
		const beskrivelse = tekst.length > 170 ? tekst.substring(0, 170) + '...' : tekst
		return (
			<div style={{ maxWidth: 200 }}>
				<p>{beskrivelse}</p>
			</div>
		)
	}

	const updateAlder = () => {
		personListe.map((person) => {
			const redigertPerson = _get(tmpPersoner?.pdlforvalter, `${person?.identNr}.person`)
			if (redigertPerson && !redigertPerson.doedsfall) {
				person.alder = person.alder.split(' ')[0]
			}
		})
	}

	if (tmpPersoner) updateAlder()

	const columns = [
		{
			text: 'Ident',
			width: '20',
			dataField: 'identNr',
			unique: true,

			formatter: (cell, row) => <CopyButton value={row.identNr} />,
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
			formatter: (cell, row) => {
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
			width: '20',
			dataField: 'kilde',
		},
		{
			text: 'Brukt',
			width: '10',
			dataField: 'ibruk',
			formatter: (cell, row) => <PersonIBrukButtonConnector ident={row.ident} />,
		},
		{
			text: '',
			width: '10',
			dataField: 'harBeskrivelse',
			centerItem: true,
			formatter: (cell, row) => {
				if (row.ident.beskrivelse) {
					return (
						<Tooltip
							overlay={getKommentarTekst(row.ident.beskrivelse)}
							placement="top"
							destroyTooltipOnHide={true}
							mouseEnterDelay={0}
							mouseLeaveDelay={0.1}
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
						</Tooltip>
					)
				}
			},
		},
	]

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
				iconItem={(bruker) => (bruker.kjonn === 'MANN' ? <ManIconItem /> : <WomanIconItem />)}
				visSide={sidetall}
				visPerson={visPerson}
				onExpand={(bruker) => (
					<PersonVisningConnector
						personId={bruker.ident.ident}
						slettedeIdenter={slettedeIdenter}
						bestillingId={bruker.ident.bestillingId[0]}
						bestillingsIdListe={bruker.ident.bestillingId}
						gruppeId={bruker.ident.gruppeId}
						iLaastGruppe={iLaastGruppe}
						brukertype={brukertype}
						setVisning={setVisning}
					/>
				)}
			/>
			{isKommentarModalOpen && selectedIdent && (
				<KommentarModal closeModal={closeKommentarModal} ident={selectedIdent} />
			)}
		</ErrorBoundary>
	)
}
