import { BestillingSammendrag } from '@/components/bestilling/sammendrag/BestillingSammendrag'
import GjenopprettConnector from '@/components/bestilling/gjenopprett/GjenopprettBestillingConnector'

import './BestillingDetaljer.less'
import { malTyper } from '@/pages/minSide/maler/MalModal'
import * as _ from 'lodash-es'
import { SlettModal } from '@/components/ui/button/SlettModal/SlettModal'
import React from 'react'
import { DollyApi } from '@/service/Api'
import { OpprettMal } from '@/pages/minSide/maler/OpprettMal'

export default function BestillingDetaljer({ bestilling, iLaastGruppe, brukerId, brukertype }) {
	const alleredeMal = Boolean(bestilling.malBestillingNavn)
	const harIdenterOpprettet = bestilling.antallIdenterOpprettet > 0 || bestilling.antallLevert > 0
	const erOrganisasjon = bestilling.hasOwnProperty('organisasjonNummer')

	const sivilstand = _.get(bestilling, 'bestilling.pdldata.person.sivilstand')
	const harRelatertPersonVedSivilstand = sivilstand?.some((item) => item.relatertVedSivilstand)
	const harLevertPersoner = bestilling.antallLevert > 0

	const nyIdent = _.get(bestilling, 'bestilling.pdldata.person.nyident')
	const harEksisterendeNyIdent = nyIdent?.some((item) => item.eksisterendeIdent)

	const forelderBarnRelasjon = _.get(bestilling, 'bestilling.pdldata.person.forelderBarnRelasjon')
	const harRelatertPersonBarn = forelderBarnRelasjon?.some((item) => item.relatertPerson)

	const gjenopprettingsId =
		bestilling?.opprettetFraGruppeId ||
		bestilling?.opprettetFraId ||
		bestilling?.gjenopprettetFraIdent

	const gjenopprettTitle = gjenopprettingsId
		? 'Kan ikke gjenopprette gjenoppretting av bestilling'
		: harLevertPersoner
			? 'Gjenopprett bestilling'
			: 'Kan ikke gjenopprette bestilling fordi den har ingen leverte identer'

	return (
		<div className="bestilling-detaljer">
			<BestillingSammendrag bestilling={bestilling} />

			{harIdenterOpprettet && !erOrganisasjon && (
				<div className="flexbox--align-center--justify-end info-block">
					{!iLaastGruppe && (
						<GjenopprettConnector
							bestilling={bestilling}
							brukerId={brukerId}
							brukertype={brukertype}
							disabled={!harLevertPersoner || gjenopprettingsId}
							title={gjenopprettTitle}
						/>
					)}
					{!alleredeMal &&
						!harRelatertPersonVedSivilstand &&
						!harEksisterendeNyIdent &&
						!harRelatertPersonBarn &&
						!gjenopprettingsId && <OpprettMal id={bestilling.id} malType={malTyper.BESTILLING} />}
					<SlettModal
						action={DollyApi.slettBestilling}
						bestillingId={bestilling.id}
						loading={false}
						navigateHome={false}
						slettType={'bestilling'}
					/>
				</div>
			)}

			{erOrganisasjon && (
				<div className="flexbox--align-center--justify-end info-block">
					<GjenopprettConnector
						bestilling={bestilling}
						brukerId={brukerId}
						brukertype={brukertype}
						disabled={!bestilling.ferdig}
						title={
							!bestilling.ferdig
								? 'Bestillingen kan ikke gjenopprettes før den er ferdig'
								: undefined
						}
					/>
					<OpprettMal id={bestilling.id} malType={malTyper.ORGANISASJON} />
				</div>
			)}
		</div>
	)
}
