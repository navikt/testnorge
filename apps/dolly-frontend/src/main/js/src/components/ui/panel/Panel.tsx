import { useToggle } from 'react-use'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import Icon from '@/components/ui/icon/Icon'
import ExpandButton from '@/components/ui/button/ExpandButton/ExpandButton'
import LinkButton from '@/components/ui/button/LinkButton/LinkButton'

import './Panel.less'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'

export default function Panel({
	startOpen = false,
	hasErrors = false,
	heading = 'Panel',
	content = null,
	children = null as any,
	checkAttributeArray = null,
	uncheckAttributeArray = null,
	informasjonstekst = null,
	iconType = null,
	forceOpen = false,
	setPanelOpen = null,
}) {
	const [isOpen, toggleOpen] = useToggle(startOpen)
	const shouldOpen = isOpen || forceOpen

	const renderContent = children ? children : content

	const check = (e) => {
		e.stopPropagation()
		checkAttributeArray()
	}

	const uncheck = (e) => {
		e.stopPropagation()
		uncheckAttributeArray()
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
		<div className={shouldOpen ? 'dolly-panel dolly-panel-open' : 'dolly-panel'}>
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
				{hasErrors && (
					<div className="dolly-panel-heading_error">
						<Icon size={16} kind="report-problem-triangle" />
						Feil i felter
					</div>
				)}
				<span className="dolly-panel-heading_buttons">
					{checkAttributeArray && (
						<LinkButton
							data-cy={
								erAvhengigAvQ1EllerQ2(heading)
									? CypressSelector.BUTTON_VELG_MILJOE_AVHENGIG
									: CypressSelector.BUTTON_VELG_ALLE
							}
							text="Velg alle"
							onClick={check}
						/>
					)}
					{uncheckAttributeArray && (
						<LinkButton
							data-cy={
								erAvhengigAvQ1EllerQ2(heading)
									? CypressSelector.BUTTON_FJERN_MILJOE_AVHENGIG
									: CypressSelector.BUTTON_FJERN_ALLE
							}
							text="Fjern alle"
							onClick={uncheck}
						/>
					)}
					<ExpandButton expanded={shouldOpen} onClick={toggleOpen} />
				</span>
			</div>
			{shouldOpen && <div className="dolly-panel-content">{renderContent}</div>}
		</div>
	)
}
