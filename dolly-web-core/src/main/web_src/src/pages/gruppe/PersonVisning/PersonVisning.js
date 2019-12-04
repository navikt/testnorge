import React from 'react'
import { useMount } from 'react-use'
import { TidligereBestillinger } from './TidligereBestillinger/TidligereBestillinger'
import { TpsfVisning } from '~/components/fagsystem/tpsf/visning/Visning'
import { KrrVisning } from '~/components/fagsystem/krrstub/visning/KrrVisning'
import { PdlfVisning } from '~/components/fagsystem/pdlf/visning/Visning'
import { ArenaVisning } from '~/components/fagsystem/arena/visning/ArenaVisning'
import { UdiVisning } from '~/components/fagsystem/udistub/visning/UdiVisning'
import { SigrunstubVisning } from '~/components/fagsystem/sigrunstub/visning/Visning'
import { InstVisning } from '~/components/fagsystem/inst/visning/InstVisning'
import BeskrivelseConnector from '~/components/beskrivelse/BeskrivelseConnector'
import { SlettModal } from '~/components/ui/slettModal/SlettModal'
import { BestillingSammendragModal } from '~/components/bestilling/sammendrag/SammendragModal'

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
			<TpsfVisning data={TpsfVisning.filterValues(data.tpsf, bestilling.tpsfKriterier)} />
			<PdlfVisning data={data.pdlforvalter} loading={loading.pdlforvalter} />

			<SigrunstubVisning data={data.sigrunstub} loading={loading.sigrunstub} />
			<KrrVisning data={data.krrstub} loading={loading.krrstub} />
			{/* <AaregVisning /> */}
			<InstVisning data={data.instdata} loading={loading.instdata} />
			<ArenaVisning
				data={data.arenaforvalteren}
				bestData={bestilling.bestKriterier.arenaforvalter}
				loading={loading.arenaforvalteren}
			/>
			<UdiVisning
				data={UdiVisning.filterValues(data.udistub, bestilling.bestKriterier.udistub)}
				loading={loading.udistub}
			/>
			<TidligereBestillinger ids={ident.bestillingId} />
			<BeskrivelseConnector ident={ident} />

			<div className="flexbox--align-center--justify-end">
				<BestillingSammendragModal bestilling={bestilling} />
				<SlettModal tekst="testpersonen" action={slettPerson} loading={loading.slettPerson} />
			</div>
		</div>
	)
}
