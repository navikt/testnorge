import React from 'react'
import { VisningType } from './Gruppe'
import Loading from '@/components/ui/loading/Loading'
import BestillingListeConnector from '@/pages/gruppe/BestillingListe/BestillingListeConnector'
import PersonListeConnector from '@/pages/gruppe/PersonListe/PersonListeConnector'

type GruppeVisningProps = {
	visning: VisningType
	erLaast: boolean
	brukertype: string
	gruppeId: string
	identer: any
	bestillingerById: any
	lasterBestillinger?: boolean
}

export const GruppeVisning: React.FC<GruppeVisningProps> = ({
	visning,
	erLaast,
	brukertype,
	gruppeId,
	identer,
	bestillingerById,
	lasterBestillinger,
}) => {
	return lasterBestillinger ? (
		<Loading label={'Laster bestillinger...'} />
	) : (
		<>
			{visning === VisningType.VISNING_PERSONER && (
				<PersonListeConnector
					iLaastGruppe={erLaast}
					brukertype={brukertype}
					gruppeId={gruppeId}
					identer={identer}
					bestillingerById={bestillingerById}
				/>
			)}
			{visning === VisningType.VISNING_BESTILLING && (
				<BestillingListeConnector
					iLaastGruppe={erLaast}
					brukertype={brukertype}
					bestillingerById={bestillingerById}
					gruppeId={gruppeId}
				/>
			)}
		</>
	)
}
