import { useToggle } from 'react-use'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import Icon from '@/components/ui/icon/Icon'
import ExpandButton from '@/components/ui/button/ExpandButton/ExpandButton'
import LinkButton from '@/components/ui/button/LinkButton/LinkButton'

import './Panel.less'
import { TestComponentSelectors } from '#/mocks/Selectors'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'
import { useEffect, useContext, useRef } from 'react'
import { InlineMessage } from '@navikt/ds-react'
import {
	containsCheckedAttributt,
	countMatchingAttributter,
	matchesFilter,
	PanelFilterContext,
} from '@/components/ui/panel/PanelFilterContext'

export default function Panel({
	startOpen = false,
	hasErrors = false,
	heading = 'Panel',
	content = null,
	children = null as any,
	checkAttributeArray = null,
	uncheckAttributeArray = null,
	informasjonstekst = null as unknown as string,
	iconType = null as unknown as string,
	forceOpen = false,
	setPanelOpen = null,
	...rest
}) {
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
	const { filterText } = useContext(PanelFilterContext)
	const [isOpen, toggleOpen] = useToggle(startOpen)

	const renderContent = children ? children : content

	const hasCheckedAttributt = containsCheckedAttributt(renderContent)
	const hadCheckedAttributt = useRef(hasCheckedAttributt)
	useEffect(() => {
		if (!hadCheckedAttributt.current && hasCheckedAttributt) {
			toggleOpen(true)
		}
		hadCheckedAttributt.current = hasCheckedAttributt
	}, [hasCheckedAttributt, toggleOpen])

	const filtering = !!filterText
	const headingMatch = filtering && matchesFilter(heading, filterText)
	const matchCount = filtering ? countMatchingAttributter(renderContent, filterText) : 0
	const filterHit = headingMatch || matchCount > 0
	const shouldOpen = isOpen || forceOpen || (filtering && filterHit)

	const check = (e) => {
		e.stopPropagation()
		checkAttributeArray()
	}

	const uncheck = (e) => {
		e.stopPropagation()
		uncheckAttributeArray()
	}

	if (filtering && !filterHit) {
		return null
	}

	const erAvhengigAvQ1EllerQ2 = (heading: string) => {
		const miljoeAvhengigeArtifakter = [
			'PENSJON',
			'ARBEIDSYTELSER',
			'INSTITUSJONSOPPHOLD',
			'DOKUMENTER',
		]
		return miljoeAvhengigeArtifakter.includes(heading.toUpperCase())
	}

	return (
		<div className={shouldOpen ? 'dolly-panel dolly-panel-open' : 'dolly-panel'} {...rest}>
			<div
				className="dolly-panel-heading"
				onClick={() => {
					setPanelOpen && setPanelOpen(!startOpen)
					toggleOpen()
				}}
			>
				{iconType && <Icon fontSize={'2.8rem'} kind={iconType} className="header-icon" />}
				<h2>{heading}</h2>

				{informasjonstekst && <Hjelpetekst>{informasjonstekst}</Hjelpetekst>}
				{hasErrors && errorContext.showError && (
					<InlineMessage status="error" size="small" style={{ marginLeft: '10px' }}>
						Feil i felter
					</InlineMessage>
				)}
				<span className="dolly-panel-heading_buttons">
					{checkAttributeArray && (
						<LinkButton
							data-testid={
								erAvhengigAvQ1EllerQ2(heading)
									? TestComponentSelectors.BUTTON_VELG_MILJOE_AVHENGIG
									: TestComponentSelectors.BUTTON_VELG_ALLE
							}
							text="Velg alle"
							onClick={check}
							disabled={!!filterText}
						/>
					)}
					{uncheckAttributeArray && (
						<LinkButton
							data-testid={
								erAvhengigAvQ1EllerQ2(heading)
									? TestComponentSelectors.BUTTON_FJERN_MILJOE_AVHENGIG
									: TestComponentSelectors.BUTTON_FJERN_ALLE
							}
							text="Fjern alle"
							onClick={uncheck}
							disabled={!!filterText}
						/>
					)}
					<ExpandButton expanded={shouldOpen} onClick={toggleOpen} />
				</span>
			</div>
			{shouldOpen && (
				<div className="dolly-panel-content">
					{headingMatch ? (
						<PanelFilterContext.Provider value={{ filterText: '' }}>
							{renderContent}
						</PanelFilterContext.Provider>
					) : (
						renderContent
					)}
				</div>
			)}
		</div>
	)
}
