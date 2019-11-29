import React from 'react'
import { useMount } from 'react-use'
import { TidligereBestillinger } from './TidligereBestillinger/TidligereBestillinger'
import { TpsfVisning } from '~/components/fagsystem/tpsf/visning/Visning'
import { KrrVisning } from '~/components/fagsystem/krrstub/visning/KrrVisning'
import { PdlfVisning } from '~/components/fagsystem/pdlf/visning/Visning'
import { ArenaVisning } from '~/components/fagsystem/arena/visning/ArenaVisning'
import { UdiVisning } from '~/components/fagsystem/udistub/visning/UdiVisning'
import { SigrunstubVisning } from '~/components/fagsystem/sigrunstub/visning/Visning'
import BeskrivelseConnector from '~/components/beskrivelse/BeskrivelseConnector'
import Button from '~/components/ui/button/Button'

import './PersonVisning.less'

export const PersonVisning = ({
	getDataFraFagsystemer,
	data,
	testIdent,
	bestilling,
	loading,
	gruppeId
}) => {
	useMount(getDataFraFagsystemer)

	return (
		<div className="person-visning">
			<TpsfVisning data={TpsfVisning.filterValues(data.tpsf, bestilling.tpsfKriterier)} />
			<PdlfVisning
				data={(console.log('data :', data), data.pdlforvalter)}
				loading={loading.pdlforvalter}
			/>

			{/* <SigrunVisning /> */}
			<SigrunstubVisning data={data.sigrunstub} loading={loading.sigrunstub} />
			<KrrVisning data={data.krrstub} loading={loading.krrstub} />
			{/* <AaregVisning /> */}
			{/* <InstVisning /> */}
			<ArenaVisning
				data={data.arenaforvalteren}
				bestData={bestilling.bestKriterier.arenaforvalter}
				loading={loading.arenaforvalteren}
			/>
			<UdiVisning
				data={UdiVisning.filterValues(data.udistub, bestilling.bestKriterier.udistub)}
				loading={loading.udistub}
			/>
			<TidligereBestillinger ids={testIdent.bestillingId} />
			<BeskrivelseConnector ident={testIdent.ident} gruppeId={gruppeId} />
			<div className="flexbox--align-center--justify-end">
				<Button className="flexbox--align-center" kind="details">
					BESTILLINGSDETALJER
				</Button>
				<Button className="flexbox--align-center" kind="edit">
					REDIGER
				</Button>

				{/* Slett kan være modal med egen komponent */}
				<Button className="flexbox--align-center" kind="trashcan">
					SLETT
				</Button>
			</div>
		</div>
	)
}
