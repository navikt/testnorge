import React, { Fragment } from 'react'
import Button from '~/components/ui/button/Button'
import useBoolean from '~/utils/hooks/useBoolean'
import Hjelpetekst from '~/components/hjelpetekst'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import FavoriteButtonConnector from '~/components/ui/button/FavoriteButton/FavoriteButtonConnector'
import { EksporterCSV } from '~/pages/gruppe/EksporterCSV/EksporterCSV'
import { SlettButton } from '~/components/ui/button/SlettButton/SlettButton'
import { LaasButton } from '~/components/ui/button/LaasButton/LaasButton.tsx'
import { Header } from '~/components/ui/header/Header'
import Formatters from '~/utils/DataFormatter'

import './GruppeHeader.less'

export default function GruppeHeader({ gruppe, identArray, isDeletingGruppe, deleteGruppe }) {
	const [visRedigerState, visRediger, skjulRediger] = useBoolean(false)

	// const erLaast = gruppe.erLaast
	const erLaast = gruppe.id === 702 // Later som om denne gruppen er låst for testing

	const headerClass = erLaast ? 'gruppe-header-laast' : 'gruppe-header'
	const gruppeNavn = erLaast ? `${gruppe.navn} (låst)` : gruppe.navn
	const iconType = erLaast ? 'lockedGroup' : 'group'

	return (
		<Fragment>
			<div className="page-header flexbox--align-center">
				<h1>{gruppeNavn}</h1>
				{erLaast && (
					<Hjelpetekst hjelpetekstFor="Låst gruppe">
						Denne gruppen er låst og kan derfor ikke endres. Kontakt team Dolly dersom du ønsker å
						låse opp gruppen.
					</Hjelpetekst>
				)}
			</div>
			<Header className={headerClass} icon={iconType}>
				<div className="flexbox">
					<Header.TitleValue title="Eier" value={gruppe.opprettetAvNavIdent} />
					<Header.TitleValue title="Antall personer" value={identArray.length} />
					<Header.TitleValue
						title="Sist endret"
						value={Formatters.formatStringDates(gruppe.datoEndret)}
					/>
					<Header.TitleValue
						title="Antall brukt"
						value={identArray.map(p => p.ibruk).filter(Boolean).length}
					/>
					<Header.TitleValue title="Hensikt" value={gruppe.hensikt} />
				</div>
				<div className="gruppe-header_actions">
					{gruppe.erEierAvGruppe && !erLaast && (
						<Button kind="edit" onClick={visRediger}>
							REDIGER
						</Button>
					)}
					{!erLaast && (
						// TODO: Send inn action og loading for laasGruppe
						<LaasButton action={deleteGruppe} loading={isDeletingGruppe}>
							Er du sikker på at du vil låse denne gruppen? <br />
							Når gruppen er låst må du kontakte team Dolly <br />
							dersom du ønsker å gjøre endringer.
						</LaasButton>
					)}
					{!erLaast && (
						<SlettButton action={deleteGruppe} loading={isDeletingGruppe}>
							Er du sikker på at du vil slette denne gruppen?
						</SlettButton>
					)}
					<EksporterCSV identer={identArray} gruppeId={gruppe.id} />
					{!gruppe.erEierAvGruppe && <FavoriteButtonConnector groupId={gruppe.id} />}
				</div>
			</Header>

			{visRedigerState && <RedigerGruppeConnector gruppe={gruppe} onCancel={skjulRediger} />}
		</Fragment>
	)
}
