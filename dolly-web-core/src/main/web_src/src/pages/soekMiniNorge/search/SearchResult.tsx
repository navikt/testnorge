import React, { useState } from 'react'
// @ts-ignore
import Tooltip from 'rc-tooltip'
// @ts-ignore
import { CopyToClipboard } from 'react-copy-to-clipboard'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
// @ts-ignore
import { HodejegerenApi } from '~/service/Api'
import { ManIconItem, WomanIconItem } from '~/components/ui/icon/IconItem'
import ResultatVisningConnecter from '~/pages/soekMiniNorge/search/ResultatVisning/ResultatVisningConnecter'
import { Feedback } from '~/components/feedback'
import { Innhold } from '../hodejegeren/types'
import Button from '~/components/ui/button/Button'
import Icon from '~/components/ui/icon/Icon'

interface SearchResultVisningProps {
	soekOptions: string
	searchActive: boolean
	soekNummer: number
	antallResultat: number
}

export const SearchResult = (props: SearchResultVisningProps) => {
	if (!props.searchActive) {
		return <ContentContainer>Ingen søk er gjort</ContentContainer>
	}
	if (props.soekOptions === '' && props.searchActive) {
		return <ContentContainer>Vennligst fyll inn en eller flere verdier å søke på.</ContentContainer>
	}

	const [showFeedback, setShowFeedback] = useState(props.searchActive)

	const columns = [
		{
			text: 'Ident',
			width: '40',
			dataField: 'personIdent.id',
			unique: true,
			formatter: (cell: string, row: Innhold) => (
				<div style={{ display: 'flex' }}>
					{row.personIdent.id}
					<CopyToClipboard text={row.personIdent.id}>
						<Tooltip
							overlay={'Kopier'}
							placement="top"
							destroyTooltipOnHide={true}
							mouseEnterDelay={0}
							mouseLeaveDelay={0.1}
						>
							<div
								onClick={event => {
									event.stopPropagation()
								}}
							>
								{
									// @ts-ignore
									<Icon kind="copy" size={15} />
								}
							</div>
						</Tooltip>
					</CopyToClipboard>
				</div>
			)
		},
		{
			text: 'Type',
			width: '20',
			dataField: 'personIdent.type'
		},
		{
			text: 'Navn',
			width: '50',
			dataField: 'navn.fornavn',
			formatter: (cell: string, row: Innhold) => {
				return row.navn.fornavn + ' ' + row.navn.slektsnavn
			}
		},
		{
			text: 'Kjønn',
			width: '20',
			dataField: 'personInfo.kjoenn'
		},
		{
			text: 'Alder',
			width: '30',
			dataField: 'personInfo.datoFoedt',
			formatter: (cell: Date, row: Innhold) => {
				const foedselsdato = new Date(row.personInfo.datoFoedt)
				const diff_ms = Date.now() - foedselsdato.getTime()
				const age_dt = new Date(diff_ms)

				return Math.abs(age_dt.getUTCFullYear() - 1970)
			}
		}
	]

	return (
		<LoadableComponent
			onFetch={() => HodejegerenApi.soek(props.soekOptions, props.antallResultat)}
			render={(data: Array<Innhold>) => {
				return (
					<div>
						{!data ? (
							<ContentContainer>Søket gav ingen resultater.</ContentContainer>
						) : (
							<DollyTable
								data={data}
								columns={columns}
								pagination
								iconItem={(bruker: Innhold) =>
									bruker.personInfo.kjoenn === 'M' ? <ManIconItem /> : <WomanIconItem />
								}
								onExpand={(bruker: Innhold) => (
									<ResultatVisningConnecter personId={bruker.personIdent.id} data={bruker} />
								)}
							/>
						)}
						{showFeedback && (
							<div className="feedback-container">
								<div className="feedback-container__close-button">
									<Button
										kind="remove-circle"
										onClick={() =>
											// @ts-ignore
											setShowFeedback(false)
										}
									/>
								</div>
								<Feedback
									label="Hvordan var din opplevelse med bruk av Søk i Mini-Norge?"
									feedbackFor="Bruk av Søk i Mini Norge"
								/>
							</div>
						)}
					</div>
				)
			}}
		/>
	)
}
