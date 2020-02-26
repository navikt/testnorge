import React from 'react'
import { useMount } from 'react-use'
import { TidligereBestillinger } from './TidligereBestillinger/TidligereBestillinger'
import { TpsfVisning } from '~/components/fagsystem/tpsf/visning/Visning'
import { KrrVisning } from '~/components/fagsystem/krrstub/visning/KrrVisning'
import { PdlfVisning } from '~/components/fagsystem/pdlf/visning/Visning'
import { ArenaVisning } from '~/components/fagsystem/arena/visning/ArenaVisning'
import { AaregVisning } from '~/components/fagsystem/aareg/visning/Visning'
import { UdiVisning } from '~/components/fagsystem/udistub/visning/UdiVisning'
import { SigrunstubVisning } from '~/components/fagsystem/sigrunstub/visning/Visning'
import { InntektstubVisning } from '~/components/fagsystem/inntektstub/visning/Visning'
import { InstVisning } from '~/components/fagsystem/inst/visning/InstVisning'
import BeskrivelseConnector from '~/components/beskrivelse/BeskrivelseConnector'
import { SlettButton } from '~/components/ui/button/SlettButton/SlettButton'
import { BestillingSammendragModal } from '~/components/bestilling/sammendrag/SammendragModal'
import { LeggTilRelasjonModal } from '~/components/leggTilRelasjon/LeggTilRelasjonModal'

import './PersonVisning.less'

export const PersonVisning = ({
	fetchDataFraFagsystemer,
	data,
	ident,
	bestilling,
	loading,
	slettPerson
}) => {
	useMount(fetchDataFraFagsystemer)

	return (
		<div className="person-visning">
			<TpsfVisning data={TpsfVisning.filterValues(data.tpsf, bestilling.bestilling.tpsf)} />
			<PdlfVisning data={data.pdlforvalter} loading={loading.pdlforvalter} />
			<AaregVisning data={data.aareg} loading={loading.aareg} />
			<SigrunstubVisning data={data.sigrunstub} loading={loading.sigrunstub} />
			<InntektstubVisning data={data.inntektstub} loading={loading.inntektstub} />
			<KrrVisning data={data.krrstub} loading={loading.krrstub} />
			<InstVisning data={data.instdata} loading={loading.instdata} />
			<ArenaVisning
				data={data.arenaforvalteren}
				bestData={bestilling.bestilling.arenaforvalter}
				loading={loading.arenaforvalteren}
			/>
			<UdiVisning
				data={UdiVisning.filterValues(data.udistub, bestilling.bestilling.udistub)}
				loading={loading.udistub}
			/>
			<TidligereBestillinger ids={ident.bestillingId} />
			<BeskrivelseConnector ident={ident} />
			<div className="person-visning_actions">
				<LeggTilRelasjonModal environments={bestilling.environments} personInfo={data.tpsf} />
				<BestillingSammendragModal bestilling={bestilling} />
				<SlettButton action={slettPerson} loading={loading.slettPerson}>
					Er du sikker på at du vil slette denne personen?
				</SlettButton>
			</div>
		</div>
	)
}
