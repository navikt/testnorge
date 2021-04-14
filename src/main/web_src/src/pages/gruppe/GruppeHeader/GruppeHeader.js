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
import GjenopprettGruppeConnector from '~/components/bestilling/gjenopprett/GjenopprettGruppeConnector'

import './GruppeHeader.less'

export default function GruppeHeader({
	gruppe,
	identArray,
	isDeletingGruppe,
	deleteGruppe,
	laasGruppe,
	isLockingGruppe,
	bestillingStatuser
}) {
	const [visRedigerState, visRediger, skjulRediger] = useBoolean(false)
	const [viserGjenopprettModal, visGjenopprettModal, skjulGjenopprettModal] = useBoolean(false)

	const erLaast = gruppe.erLaast

	const headerClass = erLaast ? 'gruppe-header-laast' : 'gruppe-header'
	const gruppeNavn = erLaast ? `${gruppe.navn} (låst)` : gruppe.navn
	const iconType = erLaast ? 'lockedGroup' : 'group'

	return (
		<Fragment>
			<div className="page-header flexbox--align-center">
				<h1>{gruppeNavn}</h1>
				{erLaast && (
					<Hjelpetekst hjelpetekstFor="Låst gruppe" type="under">
						Denne gruppen er låst. Låste grupper er velegnet for å dele med eksterne samhandlere
						fordi de ikke kan endres, og blir heller ikke påvirket av prodlast i samhandlermiljøet
						(Q1). Kontakt team Dolly dersom du ønsker å låse opp gruppen.
					</Hjelpetekst>
				)}
			</div>
			<Header className={headerClass} icon={iconType}>
				<div className="flexbox">
					<Header.TitleValue
						title="Eier"
						value={gruppe.opprettetAv.brukernavn || gruppe.opprettetAv.navIdent}
					/>
					<Header.TitleValue title="Antall personer" value={gruppe.antallIdenter} />
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
				<div className="gruppe-header__actions">
					{gruppe.erEierAvGruppe && !erLaast && (
						<Button kind="edit" onClick={visRediger}>
							REDIGER
						</Button>
					)}
					<Button
						onClick={visGjenopprettModal}
						kind="synchronize"
						disabled={gruppe.antallIdenter < 1}
						title={gruppe.antallIdenter < 1 ? 'Kan ikke gjenopprette en tom gruppe' : null}
					>
						GJENOPPRETT
					</Button>
					{!erLaast && (
						<LaasButton action={laasGruppe} loading={isLockingGruppe}>
							Er du sikker på at du vil låse denne gruppen? <br />
							En gruppe som er låst kan ikke endres, og blir heller ikke <br />
							påvirket av prodlast i samhandlermiljøet (Q1). <br />
							Når gruppen er låst må du kontakte team Dolly <br />
							dersom du ønsker å låse den opp igjen.
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
			{viserGjenopprettModal && (
				<GjenopprettGruppeConnector
					onCancel={skjulGjenopprettModal}
					gruppe={gruppe}
					bestillingStatuser={bestillingStatuser}
				/>
			)}
		</Fragment>
	)
}
