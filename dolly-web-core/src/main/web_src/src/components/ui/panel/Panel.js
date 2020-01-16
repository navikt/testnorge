import React from 'react'
import { useToggle } from 'react-use'
import cn from 'classnames'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import Icon from '~/components/ui/icon/Icon'
import ExpandButton from '~/components/ui/button/ExpandButton'
import LinkButton from '~/components/ui/button/LinkButton/LinkButton'

import './Panel.less'

export default function Panel({
	startOpen = false,
	hasErrors = false,
	heading = 'Panel',
	content,
	children,
	checkAttributeArray,
	uncheckAttributeArray,
	informasjonstekst,
	iconType
}) {
	const [isOpen, toggleOpen] = useToggle(startOpen)

	const panelClass = cn('panel', {
		'panel-open': isOpen
	})

	const renderContent = children ? children : content

	const check = e => {
		e.stopPropagation()
		checkAttributeArray()
	}

	const uncheck = e => {
		e.stopPropagation()
		uncheckAttributeArray()
	}

	return (
		<div className={panelClass}>
			<div className="panel-heading" onClick={toggleOpen}>
				{iconType && <Icon size={45} kind={iconType} className="header-icon" />}
				<h2>{heading}</h2>

				{informasjonstekst && <HjelpeTekst>{informasjonstekst}</HjelpeTekst>}
				{hasErrors && (
					<div className="panel-heading_error">
						<Icon size={16} kind="report-problem-triangle" />
						Feil i felter
					</div>
				)}
				<span className="panel-heading_buttons">
					{checkAttributeArray && <LinkButton text="Velg alle" onClick={check} />}
					{uncheckAttributeArray && <LinkButton text="Fjern alle" onClick={uncheck} />}
					<ExpandButton expanded={isOpen} onClick={toggleOpen} />
				</span>
			</div>
			{isOpen && <div className="panel-content">{renderContent}</div>}
		</div>
	)
}
