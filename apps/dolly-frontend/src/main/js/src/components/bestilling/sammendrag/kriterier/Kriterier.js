import React, { Fragment, useState } from 'react'
import cn from 'classnames'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { mapBestillingData } from './BestillingKriterieMapper'
import { OrganisasjonKriterier } from './OrganisasjonKriterier'
import DollyKjede from '~/components/dollyKjede/DollyKjede'

const _renderBestillingsDetaljer = (data) => {
	const [selectedIndex, setSelectedIndex] = useState(0)

	return data.map((kategori, j) => {
		const bottomBorder = j != data.length - 1
		const cssClass = cn('flexbox--align-start info-text', {
			'bottom-border': bottomBorder,
		})
		if (kategori.header) {
			return (
				<Fragment key={j}>
					<h4>{kategori.header} </h4>
					{kategori.items && (
						<div className={cssClass}>{kategori.items.map(_renderStaticValue)}</div>
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
							{kategori.itemRows.map((row, i) => (
								<div className="dfa-blokk" key={i}>
									{/*className endres under styling. Kun eksempel*/}
									{row[0].numberHeader && <h4>{row[0].numberHeader}</h4>}
									<div className={'flexbox--align-start flexbox--wrap'} key={i}>
										{row.map(_renderStaticValue)}
									</div>
								</div>
							))}
						</div>
					)}
				</Fragment>
			)
		}
	})
}

const _renderStaticValue = (attributt, key) => {
	if (!attributt.value) return false
	return (
		<TitleValue
			key={key}
			title={attributt.label}
			value={attributt.value}
			kodeverk={attributt.apiKodeverkId}
			size={attributt.width ? attributt.width : 'small'}
		/>
	)
}

export default function Bestillingskriterier({ bestilling, bestillingsinformasjon, header }) {
	if (bestilling.organisasjon || bestilling.enhetstype) {
		return (
			<div className="bestilling-detaljer">
				{header && <SubOverskrift label={header} />}
				<OrganisasjonKriterier
					data={bestilling.organisasjon || bestilling}
					render={_renderBestillingsDetaljer}
				/>
			</div>
		)
	}

	const data = mapBestillingData(bestilling, bestillingsinformasjon)

	if (!data) return <p>Kunne ikke hente bestillingsdata</p>

	return (
		<div className="bestilling-detaljer">
			{header && <SubOverskrift label={header} />}
			{_renderBestillingsDetaljer(data)}
		</div>
	)
}
