import React, { useEffect, useRef, useState } from 'react'
import Tooltip from 'rc-tooltip'
import { CopyToClipboard } from 'react-copy-to-clipboard'
import 'rc-tooltip/assets/bootstrap.css'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import PersonIBrukButtonConnector from '~/components/ui/button/PersonIBrukButton/PersonIBrukButtonConnector'
import PersonVisningConnector from '../PersonVisning/PersonVisningConnector'
import { ManIconItem, WomanIconItem } from '~/components/ui/icon/IconItem'

import Icon from '~/components/ui/icon/Icon'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import useBoolean from '~/utils/hooks/useBoolean'
import { KommentarModal } from '~/pages/gruppe/PersonListe/modal/KommentarModal'

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle',
}

export default function PersonListe({
	isFetching,
	personListe,
	gruppeInfo,
	identer,
	sidetall,
	setSidetall,
	sideStoerrelse,
	setSideStoerrelse,
	visPerson,
	iLaastGruppe,
	fetchTpsfPersoner,
	fetchPdlPersoner,
}) {
	const [isKommentarModalOpen, openKommentarModal, closeKommentarModal] = useBoolean(false)
	const [selectedIdent, setSelectedIdent] = useState(null)

	const previousValues = useRef(identer)
	const previousIdenter = Object.values(previousValues.current)

	const gruppeEndret = () => {
		if (!previousValues) return true
		previousValues.current = identer
		const prevIbruk = previousIdenter.map((ident) => ({
			ident: ident.ident,
			ibruk: ident.ibruk,
		}))

		const iBrukEndret = prevIbruk.some((id) => {
			if (id.ibruk !== identer?.[id.ident]?.ibruk) {
				return true
			}
		})

		const identerEndret = previousIdenter.filter((prevIdent) => !identer[prevIdent.ident])

		return !iBrukEndret || identerEndret.length > 0
	}

	useEffect(() => {
		if (gruppeEndret()) {
			fetchTpsfPersoner()
			fetchPdlPersoner()
			previousValues.current = identer
		}
	}, [identer])

	if (isFetching) return <Loading label="Laster personer" panel />

	if (visPerson && personListe && window.sessionStorage.getItem('sidetall')) {
		setSidetall(parseInt(window.sessionStorage.getItem('sidetall')))
		setSideStoerrelse(10)
		window.sessionStorage.removeItem('sidetall')
	}

	if (!personListe || personListe.length === 0)
		return (
			<ContentContainer>
				Trykk på opprett personer-knappen for å starte en bestilling.
			</ContentContainer>
		)

	const getKommentarTekst = (tekst) => {
		const beskrivelse = tekst.length > 170 ? tekst.substring(0, 170) + '...' : tekst
		return (
			<div style={{ maxWidth: 200 }}>
				<p>{beskrivelse}</p>
			</div>
		)
	}

	const columns = [
		{
			text: 'Ident',
			width: '20',
			dataField: 'identNr',
			unique: true,

			formatter: (cell, row) => (
				<div className="identnummer-cell">
					{row.identNr}
					<CopyToClipboard text={row.identNr}>
						<Tooltip
							overlay={'Kopier'}
							placement="top"
							destroyTooltipOnHide={true}
							mouseEnterDelay={0}
							mouseLeaveDelay={0.1}
							arrowContent={<div className="rc-tooltip-arrow-inner" />}
							align={{
								offset: ['0', '-10'],
							}}
						>
							<div
								className="icon"
								onClick={(event) => {
									event.stopPropagation()
								}}
							>
								<Icon kind="copy" size={15} />
							</div>
						</Tooltip>
					</CopyToClipboard>
				</div>
			),
		},
		{
			text: 'Navn',
			width: '30',
			dataField: 'navn',
		},
		{
			text: 'Alder',
			width: '10',
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
				setSidetall={setSidetall}
				setSideStoerrelse={setSideStoerrelse}
				visPerson={visPerson}
				onExpand={(bruker) => (
					<PersonVisningConnector
						personId={bruker.ident.ident}
						bestillingId={bruker.ident.bestillingId[0]}
						bestillingsIdListe={bruker.ident.bestillingId}
						gruppeId={bruker.ident.gruppeId}
						iLaastGruppe={iLaastGruppe}
						kildePdl={bruker?.kilde === 'PDL' || bruker?.kilde === 'TESTNORGE'}
					/>
				)}
			/>
			{isKommentarModalOpen && selectedIdent && (
				<KommentarModal closeModal={closeKommentarModal} ident={selectedIdent} />
			)}
		</ErrorBoundary>
	)
}
