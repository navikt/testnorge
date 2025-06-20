import React, { Fragment } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import Button from '@/components/ui/button/Button'
import useBoolean from '@/utils/hooks/useBoolean'
import { EksporterExcel } from '@/pages/gruppe/EksporterExcel/EksporterExcel'
import { SlettButton } from '@/components/ui/button/SlettButton/SlettButton'
import { LaasButton } from '@/components/ui/button/LaasButton/LaasButton'
import { Header } from '@/components/ui/header/Header'
import { arrayToString, formatStringDates } from '@/utils/DataFormatter'
import './GruppeHeader.less'
import { TagsButton } from '@/components/ui/button/Tags/TagsButton'
import { GjenopprettGruppe } from '@/components/bestilling/gjenopprett/GjenopprettGruppe'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { FlyttPersonButton } from '@/components/ui/button/FlyttPersonButton/FlyttPersonButton'
import { LeggTilPaaGruppe } from '@/pages/gruppe/LeggTilPaaGruppe/LeggTilPaaGruppe'
import cn from 'classnames'
import Icon from '@/components/ui/icon/Icon'
import { TestComponentSelectors } from '#/mocks/Selectors'
import Loading from '@/components/ui/loading/Loading'
import FavoriteButton from '@/components/ui/button/FavoriteButton/FavoriteButton'
import { RedigerGruppe } from '@/components/redigerGruppe/RedigerGruppe'
import { actions } from '@/ducks/gruppe'
import { createLoadingSelector } from '@/ducks/loading'
import { useGruppeById } from '@/utils/hooks/useGruppe'
import { EndreTilknytning } from '@/pages/gruppe/EndreTilknytning/EndreTilknytning'

const loadingSelectorSlettGruppe = createLoadingSelector(actions.remove)
const loadingSelectorSendTags = createLoadingSelector(actions.sendTags)
const loadingSelectorLaasGruppe = createLoadingSelector(actions.laas)
const loadingSelectorGetExcel = createLoadingSelector(actions.getGruppeExcelFil)

type GruppeHeaderProps = {
	gruppeId: string
}

const GruppeHeader = ({ gruppeId }: GruppeHeaderProps) => {
	const dispatch = useDispatch()
	const [visRedigerState, visRediger, skjulRediger] = useBoolean(false)
	const [viserGjenopprettModal, visGjenopprettModal, skjulGjenopprettModal] = useBoolean(false)
	const {
		currentBruker: { brukertype },
	} = useCurrentBruker()

	const isDeletingGruppe = useSelector((state: any) => loadingSelectorSlettGruppe(state))
	const isSendingTags = useSelector((state: any) => loadingSelectorSendTags(state))
	const isLockingGruppe = useSelector((state: any) => loadingSelectorLaasGruppe(state))
	const isFetchingExcel = useSelector((state: any) => loadingSelectorGetExcel(state))

	const { gruppe, error } = useGruppeById(gruppeId)

	if (error) {
		return <div>Could not load group</div>
	}
	if (!gruppe) {
		return <Loading label={'Laster gruppe...'} />
	}

	const laasGruppe = (id: number) => {
		dispatch(actions.laas(id, { erLaast: true, laastBeskrivelse: 'Låst gruppe' }))
	}
	const deleteGruppe = (id: number) => {
		dispatch(actions.remove(id))
	}

	const erLaast = gruppe.erLaast
	const headerClass = erLaast ? 'gruppe-header-laast' : 'gruppe-header'
	const gruppeNavn = erLaast ? `${gruppe.navn} (låst)` : gruppe.navn
	const iconType = erLaast ? 'locked-group' : 'group'
	const antallPersoner = gruppe.antallIdenter

	return (
		<Fragment>
			<div
				data-testid={TestComponentSelectors.TITLE_VISNING}
				className="page-header flexbox--align-center"
			>
				<h1>{gruppeNavn}</h1>
				{erLaast && (
					<Hjelpetekst placement={bottom}>
						Denne gruppen er låst. Låste grupper er velegnet for å dele med eksterne samhandlere
						fordi de ikke kan endres, og blir heller ikke påvirket av prodlast i samhandlermiljøet
						(Q1). Kontakt Team Dolly dersom du ønsker å låse opp gruppen.
					</Hjelpetekst>
				)}
			</div>
			<header className={cn('content-header', headerClass)}>
				<div className="content-header_content">
					<div className="flexbox">
						<div className={`content-header_icon ${headerClass}`}>
							<Icon kind={iconType} fontSize={'2.5rem'} />
						</div>
						<Header.TitleValue
							title="Eier"
							value={gruppe.opprettetAv?.brukernavn || gruppe.opprettetAv?.navIdent}
						/>
						<Header.TitleValue title="Antall personer" value={antallPersoner} />
						<Header.TitleValue title="Sist endret" value={formatStringDates(gruppe.datoEndret)} />
						<Header.TitleValue title="Hensikt" value={gruppe.hensikt} />
						{gruppe.tags && (
							<Header.TitleValue
								title="Tags"
								value={arrayToString(
									gruppe.tags?.length > 1 ? [...gruppe.tags].sort() : gruppe.tags,
								)}
							/>
						)}
					</div>
					<div className="gruppe-header__border" />
					<div className="gruppe-header__actions">
						{!erLaast && <LeggTilPaaGruppe antallPersoner={antallPersoner} gruppeId={gruppe.id} />}
						{!erLaast && <FlyttPersonButton gruppeId={gruppe.id} disabled={antallPersoner < 1} />}
						{!erLaast && <EndreTilknytning gruppe={gruppe} />}
						{gruppe.erEierAvGruppe && !erLaast && (
							<Button
								data-testid={TestComponentSelectors.BUTTON_REDIGER_GRUPPE}
								kind="edit"
								onClick={visRediger}
							>
								REDIGER
							</Button>
						)}
						<Button
							data-testid={TestComponentSelectors.BUTTON_GJENOPPRETT_GRUPPE}
							onClick={visGjenopprettModal}
							kind="synchronize"
							disabled={antallPersoner < 1}
							title={antallPersoner < 1 ? 'Kan ikke gjenopprette en tom gruppe' : null}
						>
							GJENOPPRETT
						</Button>
						{gruppe.erEierAvGruppe && !erLaast && (
							<LaasButton gruppeId={gruppe.id} action={laasGruppe} loading={isLockingGruppe}>
								Er du sikker på at du vil låse denne gruppen? <br />
								En gruppe som er låst kan ikke endres, og blir heller ikke <br />
								påvirket av prodlast i samhandlermiljøet (Q1). <br />
								Når gruppen er låst må du kontakte Team Dolly <br />
								dersom du ønsker å låse den opp igjen.
							</LaasButton>
						)}
						{gruppe.erEierAvGruppe && !erLaast && (
							<SlettButton
								gruppeId={gruppe.id}
								action={deleteGruppe}
								loading={isDeletingGruppe}
								navigateHome={true}
							>
								Er du sikker på at du vil slette denne gruppen?
							</SlettButton>
						)}
						{brukertype !== 'BANKID' && (
							<TagsButton
								isSending={isSendingTags}
								gruppeId={gruppe.id}
								eksisterendeTags={gruppe.tags}
							/>
						)}
						<EksporterExcel gruppeId={gruppe.id} />
						{!gruppe.erEierAvGruppe && <FavoriteButton groupId={gruppe.id} />}
					</div>
				</div>
			</header>
			{visRedigerState && <RedigerGruppe gruppeId={gruppeId} onCancel={skjulRediger} />}
			{viserGjenopprettModal && (
				<GjenopprettGruppe onClose={skjulGjenopprettModal} gruppeId={gruppeId} />
			)}
		</Fragment>
	)
}

export default GruppeHeader
