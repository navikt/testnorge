import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { FullmaktValues } from '@/components/fagsystem/pdlf/PdlTypes'
import { formatDate, omraaderArrayToString } from '@/utils/DataFormatter'
import { EkspanderbarVisning } from '@/components/bestilling/sammendrag/visning/EkspanderbarVisning'

type FullmaktTypes = {
	fullmaktListe: Array<FullmaktValues>
}

export const Fullmakt = ({ fullmaktListe }: FullmaktTypes) => {
	if (!fullmaktListe || fullmaktListe.length < 1) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Fullmakt</BestillingTitle>
				<DollyFieldArray header="Fullmakt" data={fullmaktListe}>
					{(fullmakt: FullmaktValues, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue title="Områder" value={omraaderArrayToString(fullmakt.omraader)} />

								<TitleValue title="Gyldig f.o.m." value={formatDate(fullmakt.gyldigFraOgMed)} />
								<TitleValue title="Gyldig t.o.m." value={formatDate(fullmakt.gyldigTilOgMed)} />
								<TitleValue title="Fullmektig" value={fullmakt.motpartsPersonident} />
								<EkspanderbarVisning data={fullmakt.nyFullmektig} header={'FULLMEKTIG'} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
