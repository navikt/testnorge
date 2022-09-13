import React, { Fragment } from 'react'
import Button from '~/components/ui/button/Button'
import useBoolean from '~/utils/hooks/useBoolean'
import Hjelpetekst from '~/components/hjelpetekst'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import FavoriteButtonConnector from '~/components/ui/button/FavoriteButton/FavoriteButtonConnector'
import { EksporterExcel } from '~/pages/gruppe/EksporterExcel/EksporterExcel'
import { SlettButton } from '~/components/ui/button/SlettButton/SlettButton'
import { LaasButton } from '~/components/ui/button/LaasButton/LaasButton'
import { Header } from '~/components/ui/header/Header'
import Formatters from '~/utils/DataFormatter'

import './GruppeHeader.less'
import { TagsButton } from '~/components/ui/button/Tags/TagsButton'
import { PopoverOrientering } from 'nav-frontend-popover'
import { GjenopprettGruppe } from '~/components/bestilling/gjenopprett/GjenopprettGruppe'
import { useGruppeById } from '~/utils/hooks/useGruppe'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { FlyttPersonButton } from '~/components/ui/button/FlyttPersonButton/FlyttPersonButton'

type GruppeHeaderProps = {
	gruppeId: number
	laasGruppe: Function
	isLockingGruppe: boolean
	deleteGruppe: Function
	isDeletingGruppe: boolean
	getGruppeExcelFil: Function
	isFetchingExcel: boolean
	isSendingTags: boolean
	sendTags: Function
}

const GruppeHeader = ({
	gruppeId,
	deleteGruppe,
	isDeletingGruppe,
	getGruppeExcelFil,
	isFetchingExcel,
	laasGruppe,
	isLockingGruppe,
	sendTags,
	isSendingTags,
}: GruppeHeaderProps) => {
	const [visRedigerState, visRediger, skjulRediger] = useBoolean(false)
	const [viserGjenopprettModal, visGjenopprettModal, skjulGjenopprettModal] = useBoolean(false)
	const { gruppe } = useGruppeById(gruppeId)
	const {
		currentBruker: { brukertype },
	} = useCurrentBruker()

	const erLaast = gruppe.erLaast

	const headerClass = erLaast ? 'gruppe-header-laast' : 'gruppe-header'
	const gruppeNavn = erLaast ? `${gruppe.navn} (låst)` : gruppe.navn
	const iconType = erLaast ? 'lockedGroup' : 'group'
	const antallPersoner = gruppe.antallIdenter

	console.log('gruppe: ', gruppe) //TODO - SLETT MEG

	return (
		<Fragment>
			<div className="page-header flexbox--align-center">
				<h1>{gruppeNavn}</h1>
				{erLaast && (
					<Hjelpetekst hjelpetekstFor="Låst gruppe" type={PopoverOrientering.Under}>
						Denne gruppen er låst. Låste grupper er velegnet for å dele med eksterne samhandlere
						fordi de ikke kan endres, og blir heller ikke påvirket av prodlast i samhandlermiljøet
						(Q1). Kontakt Team Dolly dersom du ønsker å låse opp gruppen.
					</Hjelpetekst>
				)}
			</div>
			<Header className={headerClass} icon={iconType}>
				<div className="flexbox">
					<Header.TitleValue
						title="Eier"
						value={gruppe.opprettetAv.brukernavn || gruppe.opprettetAv.navIdent}
					/>
					<Header.TitleValue title="Antall personer" value={antallPersoner} />
					<Header.TitleValue
						title="Sist endret"
						value={Formatters.formatStringDates(gruppe.datoEndret)}
					/>
					<Header.TitleValue title="Hensikt" value={gruppe.hensikt} />
					{gruppe.tags && (
						<Header.TitleValue
							title="Tags"
							value={Formatters.arrayToString(
								gruppe.tags?.length > 1 ? [...gruppe.tags].sort() : gruppe.tags
							)}
						/>
					)}
				</div>
				<div className="gruppe-header__actions">
					{gruppe.erEierAvGruppe && !erLaast && (
						<Button kind="edit" onClick={visRediger}>
							REDIGER
						</Button>
					)}
					{!erLaast && <FlyttPersonButton gruppeId={gruppeId} disabled={antallPersoner < 1} />}
					<Button
						onClick={visGjenopprettModal}
						kind="synchronize"
						disabled={antallPersoner < 1}
						title={antallPersoner < 1 ? 'Kan ikke gjenopprette en tom gruppe' : null}
					>
						GJENOPPRETT
					</Button>
					{!erLaast && (
						<LaasButton gruppeId={gruppe.id} action={laasGruppe} loading={isLockingGruppe}>
							Er du sikker på at du vil låse denne gruppen? <br />
							En gruppe som er låst kan ikke endres, og blir heller ikke <br />
							påvirket av prodlast i samhandlermiljøet (Q1). <br />
							Når gruppen er låst må du kontakte Team Dolly <br />
							dersom du ønsker å låse den opp igjen.
						</LaasButton>
					)}
					{!erLaast && (
						<SlettButton
							gruppeId={gruppe.id}
							action={deleteGruppe}
							loading={isDeletingGruppe}
							navigateHome={true}
						>
							Er du sikker på at du vil slette denne gruppen?
						</SlettButton>
					)}
					<EksporterExcel
						gruppeId={gruppe.id}
						action={getGruppeExcelFil}
						loading={isFetchingExcel}
					/>
					{brukertype !== 'BANKID' && (
						<TagsButton
							loading={isSendingTags}
							action={sendTags}
							gruppeId={gruppe.id}
							eksisterendeTags={gruppe.tags}
						/>
					)}
					{!gruppe.erEierAvGruppe && <FavoriteButtonConnector groupId={gruppe.id} />}
				</div>
			</Header>

			{visRedigerState && <RedigerGruppeConnector gruppe={gruppe} onCancel={skjulRediger} />}
			{viserGjenopprettModal && (
				<GjenopprettGruppe onClose={skjulGjenopprettModal} gruppe={gruppe} />
			)}
		</Fragment>
	)
}
export default GruppeHeader
