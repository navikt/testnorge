import React, { Fragment } from 'react'
import cn from 'classnames'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { mapBestillingData } from './BestillingKriterieMapper'

const _renderBestillingsDetaljer = data => {
	return data.map((kategori, j) => {
		const bottomBorder = j != data.length - 1
		const cssClass = cn('flexbox--align-center info-text', {
			'bottom-border': bottomBorder
		})
		if (kategori.header) {
			return (
				<Fragment key={j}>
					<h4>{kategori.header} </h4>
					{kategori.items && (
						<div className={cssClass}>{kategori.items.map(_renderStaticValue)}</div>
					)}
					{kategori.itemRows && (
						<div className={cn('info-text', { 'bottom-border': bottomBorder })}>
							{kategori.itemRows.map((row, i) => (
								<div className={'flexbox--align-start flexbox--wrap'} key={i}>
									{row.map(_renderStaticValue)}
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

export default function Bestillingskriterier({ bestilling, header }) {
	const data = mapBestillingData(bestilling)

	if (!data) return <p>Kunne ikke hente bestillingsdata</p>

	return (
		<div>
			{header && <SubOverskrift label={header} />}
			{_renderBestillingsDetaljer(data)}
		</div>
	)
}
