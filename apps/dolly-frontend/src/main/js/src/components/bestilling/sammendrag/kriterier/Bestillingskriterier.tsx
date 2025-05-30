import React, { Fragment, useState } from 'react'
import cn from 'classnames'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { mapBestillingData } from './BestillingKriterieMapper'
import { OrganisasjonKriterier } from './OrganisasjonKriterier'
import DollyKjede from '@/components/dollyKjede/DollyKjede'
import Button from '@/components/ui/button/Button'
import useBoolean from '@/utils/hooks/useBoolean'
import StyledAlert from '@/components/ui/alert/StyledAlert'

const _renderBestillingsDetaljer = (data) => {
	const [selectedIndex, setSelectedIndex] = useState(0)

	return data.map((kategori, j) => {
		const bottomBorder = j != data.length - 1
		const cssClass = cn('flexbox--align-start flexbox--wrap', {
			'bottom-border': bottomBorder,
		})
		if (kategori.header) {
			return (
				<Fragment key={j}>
					<h4>{kategori.header} </h4>
					{kategori.items && kategori.items.length > 0 && (
						<>
							{kategori.items[0].header && <h4>{kategori.items[0].header}</h4>}
							<div className={cssClass}>{kategori.items.map(_renderStaticValue)}</div>
						</>
					)}
					{kategori.paginering?.length > 0 && (
						<>
							<DollyKjede
								objectList={kategori.pagineringPages}
								itemLimit={10}
								selectedIndex={selectedIndex}
								setSelectedIndex={setSelectedIndex}
								isLocked={false}
							/>
							<div className={cn('info-text', { 'bottom-border': bottomBorder })}>
								{kategori.paginering[selectedIndex].itemRows.map((row, i) => (
									<div className="dfa-blokk" key={i}>
										{row[0].numberHeader && <h4>{row[0].numberHeader}</h4>}
										<div className={'flexbox--align-start flexbox--wrap'} key={i}>
											{row.map(_renderStaticValue)}
										</div>
									</div>
								))}
							</div>
						</>
					)}
					{kategori.itemRows && (
						<div className={cn('info-text', { 'bottom-border': bottomBorder })}>
							{kategori.itemRows.map((row, idx) => {
								return (
									<div className="dfa-blokk" key={idx}>
										{/*className endres under styling. Kun eksempel*/}
										{row?.[0].numberHeader && <h4>{row[0].numberHeader}</h4>}
										<div className={'flexbox--align-start flexbox--wrap'} key={idx}>
											{row?.map((attributt, idy) => {
												if (attributt?.nestedItemRows) {
													return (
														<React.Fragment key={idy}>
															{attributt.nestedItemRows.map((nestedItem, y) => {
																return (
																	<div
																		className="dfa-blokk"
																		key={y}
																		style={{ backgroundColor: 'unset' }}
																	>
																		{nestedItem?.[0].numberHeader && (
																			<h4 key={'tittel' + y}>{nestedItem?.[0].numberHeader}</h4>
																		)}
																		<div
																			className={'flexbox--align-start flexbox--wrap'}
																			key={'values' + y}
																		>
																			{nestedItem?.[1]?.map((item, z) => {
																				return _renderStaticValue(item, z)
																			})}
																		</div>
																	</div>
																)
															})}
														</React.Fragment>
													)
												}
												return attributt.expandableHeader ? (
													<RenderExpandablePanel attributt={attributt} key={idy} />
												) : (
													_renderStaticValue(attributt, idy)
												)
											})}
										</div>
									</div>
								)
							})}
						</div>
					)}
				</Fragment>
			)
		}
	})
}

export const _renderStaticValue = (attributt, idx) => {
	if (!attributt.value) {
		return null
	}
	return (
		<TitleValue
			key={idx}
			title={attributt.label}
			value={attributt.value}
			kodeverk={attributt.apiKodeverkId}
			size={attributt.width ? attributt.width : 'small'}
		/>
	)
}

const RenderExpandablePanel = ({ attributt }) => {
	const [visPersonValg, setVisPersonValg, setSkjulPersonValg] = useBoolean(false)

	if (!attributt.vis || !attributt.objects || attributt.objects.length < 1) {
		return null
	}

	return (
		<div className="flexbox--full-width">
			<Button
				onClick={visPersonValg ? setSkjulPersonValg : setVisPersonValg}
				kind={visPersonValg ? 'chevron-up' : 'chevron-down'}
				style={visPersonValg ? { margin: '10px 0 10px 0' } : { margin: '10px 0 0 0' }}
			>
				{attributt.expandableHeader}
			</Button>
			{visPersonValg && (
				<div className={'flexbox--align-start flexbox--wrap'} style={{ margin: '10px 0 10px 0' }}>
					{attributt.objects.map((attr, idx) => _renderStaticValue(attr, idx))}
				</div>
			)}
		</div>
	)
}

export default ({
	bestilling,
	bestillingsinformasjon,
	firstIdent,
	header,
	erMalVisning = false,
}) => {
	const cn = erMalVisning ? 'bestilling-detaljer malbestilling' : 'bestilling-detaljer'

	if (bestilling?.organisasjon || bestilling?.enhetstype) {
		return (
			<div className={cn}>
				{header && <SubOverskrift label={header} />}
				<OrganisasjonKriterier
					data={bestilling.organisasjon || bestilling}
					render={_renderBestillingsDetaljer}
				/>
			</div>
		)
	}

	const data = mapBestillingData(bestilling, bestillingsinformasjon, firstIdent)

	if (!data || data?.length < 1) {
		if (erMalVisning) {
			return (
				<StyledAlert variant={'warning'} size={'small'} inline>
					Denne malen inneholder ingen bestillingsdata
				</StyledAlert>
			)
		}
		return <p>Kunne ikke hente bestillingsdata</p>
	}

	return (
		<div className={cn}>
			{header && <SubOverskrift label={header} />}
			{_renderBestillingsDetaljer(data)}
		</div>
	)
}
