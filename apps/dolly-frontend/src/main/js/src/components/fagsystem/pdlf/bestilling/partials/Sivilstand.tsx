import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { SivilstandData } from '@/components/fagsystem/pdlf/PdlTypes'
import { formatDate, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import { EkspanderbarVisning } from '@/components/bestilling/sammendrag/visning/EkspanderbarVisning'

type SivilstandTypes = {
	sivilstandListe: Array<SivilstandData>
}

export const Sivilstand = ({ sivilstandListe }: SivilstandTypes) => {
	if (!sivilstandListe || sivilstandListe.length < 1) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Sivilstand (partner)</BestillingTitle>
				<DollyFieldArray header="Sivilstand" data={sivilstandListe}>
					{(sivilstand: SivilstandData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Type sivilstand"
									value={showLabel('sivilstandType', sivilstand.type)}
								/>
								<TitleValue title="Gyldig f.o.m." value={formatDate(sivilstand.sivilstandsdato)} />
								<TitleValue
									title="Bekreftelsesdato"
									value={formatDate(sivilstand.bekreftelsesdato)}
								/>
								<TitleValue
									title="Bor ikke sammen"
									value={oversettBoolean(sivilstand.borIkkeSammen)}
								/>
								<TitleValue
									title="Partner"
									value={oversettBoolean(sivilstand.relatertVedSivilstand)}
								/>
								<EkspanderbarVisning data={sivilstand.nyRelatertPerson} header={'PARTNER'} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
