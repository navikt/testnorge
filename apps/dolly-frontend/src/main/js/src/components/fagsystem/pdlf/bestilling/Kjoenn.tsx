import { KjoennValues } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { showLabel } from '@/utils/DataFormatter'

type KjoennTypes = {
	kjoennListe: Array<KjoennValues>
}

export const Kjoenn = ({ kjoennListe }: KjoennTypes) => {
	if (!kjoennListe || kjoennListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Kjønn</BestillingTitle>
				<DollyFieldArray header="Kjønn" data={kjoennListe}>
					{(kjoenn: KjoennValues, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue title="Kjønn" value={showLabel('kjoenn', kjoenn.kjoenn)} />
								<TitleValue title="Master" value={kjoenn.master} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
