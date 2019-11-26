import React from 'react'
import { useMount } from 'react-use'
import { TidligereBestillinger } from './TidligereBestillinger/TidligereBestillinger'
import { TpsfVisning } from '~/components/fagsystem/tpsf/visning/Visning'
import { KrrVisning } from '~/components/fagsystem/krrstub/visning/KrrVisning'
import { ArenaVisning } from '~/components/fagsystem/arena/visning/ArenaVisning'
import { UdiVisning } from '~/components/fagsystem/udistub/visning/UdiVisning'
import Button from '~/components/ui/button/Button'

import './PersonVisning.less'

export const PersonVisning = ({ getDataFraFagsystemer, data, testIdent, bestilling, loading }) => {
	useMount(getDataFraFagsystemer)

	return (
		<div className="person-visning">
			<TpsfVisning data={TpsfVisning.filterValues(data.tpsf, bestilling.tpsfKriterier)} />
			{/* <PdlVisning /> */}
			{/* <SigrunVisning /> */}
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
