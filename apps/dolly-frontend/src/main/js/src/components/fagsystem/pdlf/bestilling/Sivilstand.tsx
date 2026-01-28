import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { SivilstandData } from '@/components/fagsystem/pdlf/PdlTypes'
import { formatDate, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import { EkspanderbarVisning } from '@/components/bestilling/sammendrag/partials/EkspanderbarVisning'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { RelatertPerson } from '@/components/bestilling/sammendrag/partials/RelatertPerson'

type SivilstandTypes = {
	sivilstandListe: Array<SivilstandData>
}

export const Sivilstand = ({ sivilstandListe }: SivilstandTypes) => {
	if (!sivilstandListe || sivilstandListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Sivilstand (partner)</BestillingTitle>
				<DollyFieldArray header="Sivilstand" data={sivilstandListe}>
					{(sivilstand: SivilstandData, idx: number) => {
						const dataIsEmpty = (data: any) => {
							return !data || isEmpty(data, ['syntetisk'])
						}
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
								<TitleValue title="Bor ikke sammen" value={sivilstand.borIkkeSammen && 'Ja'} />
								<TitleValue
									title="Partner"
									value={oversettBoolean(sivilstand.relatertVedSivilstand)}
								/>
								<TitleValue title="Master" value={sivilstand.master} />
								<EkspanderbarVisning
									vis={!dataIsEmpty(sivilstand.nyRelatertPerson)}
									header={'PARTNER'}
								>
									<RelatertPerson personData={sivilstand.nyRelatertPerson} tittel="Partner" />
								</EkspanderbarVisning>
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
