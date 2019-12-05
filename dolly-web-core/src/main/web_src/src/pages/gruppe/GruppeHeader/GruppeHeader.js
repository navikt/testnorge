import React, { Fragment } from 'react'
import Button from '~/components/ui/button/Button'
import useBoolean from '~/utils/hooks/useBoolean'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import GruppeDetaljer from '~/pages/gruppe/GruppeDetaljer/GruppeDetaljer'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import FavoriteButtonConnector from '~/components/ui/button/FavoriteButton/FavoriteButtonConnector'
import EksporterExcel from '~/pages/gruppe/EksporterExcel/EksporterExcel'
import { SlettButton } from '~/components/ui/button/SlettButton/SlettButton'

import './GruppeHeader.less'

export default function GruppeHeader({ gruppe, identArray, isDeletingGruppe, deleteGruppe }) {
	const [visRedigerState, visRediger, skjulRediger] = useBoolean(false)

	return (
		<Fragment>
			<div className="header-valg">
				<Overskrift label={gruppe.navn}>
					{gruppe.erEierAvGruppe ? (
						// Vise redigeringsknapp eller favoriseringsstjerne
						<Button className="flexbox--align-center" kind="edit" onClick={visRediger}>
							REDIGER
						</Button>
					) : (
						<FavoriteButtonConnector groupId={gruppe.id} />
					)}
					<SlettButton action={deleteGruppe} loading={isDeletingGruppe}>
						Er du sikker på at du vil slette denne gruppen?
					</SlettButton>
				</Overskrift>
				<div className="hoyre">
					<EksporterExcel identer={identArray} gruppeId={gruppe.id} />
				</div>
			</div>

			{visRedigerState && <RedigerGruppeConnector gruppe={gruppe} onCancel={skjulRediger} />}

			<GruppeDetaljer gruppe={gruppe} identArray={identArray} />
		</Fragment>
	)
}
