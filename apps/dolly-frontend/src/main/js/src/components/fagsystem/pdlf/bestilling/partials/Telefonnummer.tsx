import { TelefonData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'

type TelefonnummerTypes = {
	telefonnummerListe: Array<TelefonData>
}

export const Telefonnummer = ({ telefonnummerListe }: TelefonnummerTypes) => {
	if (!telefonnummerListe || telefonnummerListe.length < 1) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Telefonnummer</BestillingTitle>
				<DollyFieldArray header="Telefonnummer" data={telefonnummerListe}>
					{(telefonnummer: TelefonData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Telefonnummer"
									value={`${telefonnummer.landskode} ${telefonnummer.nummer}`}
								/>
								<TitleValue title="Prioritet" value={telefonnummer.prioritet} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
