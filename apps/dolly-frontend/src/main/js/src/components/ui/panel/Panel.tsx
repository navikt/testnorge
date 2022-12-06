import { useToggle } from 'react-use'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import Icon from '@/components/ui/icon/Icon'
import ExpandButton from '@/components/ui/button/ExpandButton/ExpandButton'
import LinkButton from '@/components/ui/button/LinkButton/LinkButton'

import './Panel.less'

export default function Panel({
	startOpen = false,
	hasErrors = false,
	heading = 'Panel',
	content = null,
	children = null,
	checkAttributeArray = null,
	uncheckAttributeArray = null,
	informasjonstekst = null,
	iconType,
	forceOpen = false,
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

	return (
		<div className={shouldOpen ? 'dolly-panel dolly-panel-open' : 'dolly-panel'}>
			<div className="dolly-panel-heading" onClick={toggleOpen}>
				{iconType && <Icon size={45} kind={iconType} className="header-icon" />}
				<h2>{heading}</h2>

				{informasjonstekst && <Hjelpetekst>{informasjonstekst}</Hjelpetekst>}
				{hasErrors && (
					<div className="dolly-panel-heading_error">
						<Icon size={16} kind="report-problem-triangle" />
						Feil i felter
					</div>
				)}
				<span className="dolly-panel-heading_buttons">
					{checkAttributeArray && <LinkButton text="Velg alle" onClick={check} />}
					{uncheckAttributeArray && <LinkButton text="Fjern alle" onClick={uncheck} />}
					<ExpandButton expanded={shouldOpen} onClick={toggleOpen} />
				</span>
			</div>
			{shouldOpen && <div className="dolly-panel-content">{renderContent}</div>}
		</div>
	)
}
