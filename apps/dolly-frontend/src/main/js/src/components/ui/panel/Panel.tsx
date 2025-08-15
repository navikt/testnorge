import React, { PropsWithChildren, useCallback } from 'react'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import Icon from '@/components/ui/icon/Icon'
import ExpandButton from '@/components/ui/button/ExpandButton/ExpandButton'
import LinkButton from '@/components/ui/button/LinkButton/LinkButton'
import { TestComponentSelectors } from '#/mocks/Selectors'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'
import { useToggle } from 'react-use'
import './Panel.less'

export interface PanelProps {
	heading: string
	startOpen?: boolean
	forceOpen?: boolean
	hasErrors?: boolean
	informasjonstekst?: string | null
	iconType?: string | null
	checkAttributeArray?: (() => void) | null
	uncheckAttributeArray?: (() => void) | null
	setPanelOpen?: ((open: boolean) => void) | null
}

const envDependent = ['PENSJON', 'ARBEIDSYTELSER', 'INSTITUSJONSOPPHOLD', 'DOKUMENTER']

export const Panel: React.FC<PropsWithChildren<PanelProps>> = ({
	heading,
	startOpen = false,
	forceOpen = false,
	hasErrors = false,
	iconType,
	informasjonstekst,
	checkAttributeArray,
	uncheckAttributeArray,
	setPanelOpen,
	children,
}) => {
	const errorCtx: ShowErrorContextType = React.useContext(ShowErrorContext)
	const [isOpen, toggle] = useToggle(startOpen)
	const open = forceOpen || isOpen
	const isEnvDependent = envDependent.includes(heading.toUpperCase())

	const handleToggle = useCallback(() => {
		setPanelOpen?.(!open)
		toggle()
	}, [open, setPanelOpen, toggle])

	return (
		<section className={open ? 'dolly-panel dolly-panel-open' : 'dolly-panel'} aria-label={heading}>
			<header className="dolly-panel-heading" onClick={handleToggle}>
				{iconType && <Icon fontSize="2.8rem" kind={iconType} className="header-icon" />}
				<h2>{heading}</h2>
				{informasjonstekst && <Hjelpetekst>{informasjonstekst}</Hjelpetekst>}
				{hasErrors && errorCtx.showError && (
					<div className="dolly-panel-heading_error" aria-live="polite">
						<Icon size={16} kind="report-problem-triangle" />
						Feil i felter
					</div>
				)}
				<span className="dolly-panel-heading_buttons" onClick={(e) => e.stopPropagation()}>
					{checkAttributeArray && (
						<LinkButton
							data-testid={
								isEnvDependent
									? TestComponentSelectors.BUTTON_VELG_MILJOE_AVHENGIG
									: TestComponentSelectors.BUTTON_VELG_ALLE
							}
							text="Velg alle"
							onClick={checkAttributeArray}
						/>
					)}
					{uncheckAttributeArray && (
						<LinkButton
							data-testid={
								isEnvDependent
									? TestComponentSelectors.BUTTON_FJERN_MILJOE_AVHENGIG
									: TestComponentSelectors.BUTTON_FJERN_ALLE
							}
							text="Fjern alle"
							onClick={uncheckAttributeArray}
						/>
					)}
					<ExpandButton expanded={open} onClick={toggle} />
				</span>
			</header>
			{open && <div className="dolly-panel-content">{children}</div>}
		</section>
	)
}
export default Panel
