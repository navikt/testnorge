import { useToggle } from 'react-use'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import Icon from '@/components/ui/icon/Icon'
import ExpandButton from '@/components/ui/button/ExpandButton/ExpandButton'
import LinkButton from '@/components/ui/button/LinkButton/LinkButton'
import './Panel.less'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { ShowErrorContext, ShowErrorContextType } from '@/components/bestillingsveileder/ShowErrorContext'
import { useCallback, useContext, useState } from 'react'
import { useFormContext } from 'react-hook-form'

type PanelProps = {
	startOpen?: boolean
	hasErrors?: boolean
	heading?: React.ReactNode
	content?: React.ReactNode
	children?: React.ReactNode
	checkAttributeArray?: () => Promise<any>
	uncheckAttributeArray?: () => Promise<any>
	informasjonstekst?: string
	iconType?: string
	forceOpen?: boolean
	setPanelOpen?: (isOpen: boolean) => void
}

export default function Panel({
	startOpen = false,
	hasErrors = false,
	heading = 'Panel',
	content = null,
	children = null,
	checkAttributeArray,
	uncheckAttributeArray,
	informasjonstekst,
	iconType,
	forceOpen = false,
	setPanelOpen,
}: PanelProps) {
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
	const { setValue } = useFormContext() || {}
	const [isOpen, toggleOpen] = useToggle(startOpen)
	const [isProcessing, setIsProcessing] = useState(false)
	const shouldOpen = isOpen || forceOpen

	const renderContent = children ? children : content

	// Execute batch operations safely
	const executeBatchOperation = useCallback(
		async (operation: () => Promise<any>) => {
			if (!operation || isProcessing) return

			setIsProcessing(true)
			try {
				const result = await operation()
				// Add a slight delay to ensure UI updates
				await new Promise((resolve) => setTimeout(resolve, 50))
				setValue?.('_triggerRender', Date.now(), { shouldDirty: false })
				return result
			} catch (error) {
				console.error('Error executing batch operation:', error)
			} finally {
				setIsProcessing(false)
			}
		},
		[isProcessing, setValue],
	)

	// Handle "Velg alle" button click
	const handleVelgAlle = useCallback(
		(e: React.MouseEvent<HTMLButtonElement>) => {
			e.preventDefault()
			e.stopPropagation()

			if (checkAttributeArray) {
				executeBatchOperation(checkAttributeArray)
			}
		},
		[checkAttributeArray, executeBatchOperation],
	)

	// Handle "Fjern alle" button click
	const handleFjernAlle = useCallback(
		(e: React.MouseEvent<HTMLButtonElement>) => {
			e.preventDefault()
			e.stopPropagation()

			if (uncheckAttributeArray) {
				executeBatchOperation(uncheckAttributeArray)
			}
		},
		[uncheckAttributeArray, executeBatchOperation],
	)

	// Handle panel open/close toggle
	const handleTogglePanel = useCallback(() => {
		setPanelOpen?.((prev) => !prev)
		toggleOpen()
	}, [setPanelOpen, toggleOpen])

	// Helper to check if heading is environment dependent
	const erAvhengigAvQ1EllerQ2 = useCallback((heading: React.ReactNode) => {
		if (typeof heading !== 'string') {
			return false
		}
		const miljoeAvhengigeArtifakter = [
			'PENSJON',
			'ARBEIDSYTELSER',
			'INSTITUSJONSOPPHOLD',
			'DOKUMENTER',
		]
		return miljoeAvhengigeArtifakter.includes(heading.toUpperCase())
	}, [])

	return (
		<div className={shouldOpen ? 'dolly-panel dolly-panel-open' : 'dolly-panel'}>
			<div className="dolly-panel-heading" onClick={handleTogglePanel}>
				{iconType && <Icon fontSize={'2.8rem'} kind={iconType} className="header-icon" />}
				<h2>{heading}</h2>

				{informasjonstekst && <Hjelpetekst>{informasjonstekst}</Hjelpetekst>}
				{hasErrors && errorContext?.showError && (
					<div className="dolly-panel-heading_error">
						<Icon size={16} kind="report-problem-triangle" />
						Feil i felter
					</div>
				)}
				<span className="dolly-panel-heading_buttons">
					<LinkButton
						data-testid={TestComponentSelectors.BUTTON_VELG_ALLE}
						text="Velg alle"
						onClick={handleVelgAlle}
						disabled={isProcessing}
					/>
					<LinkButton
						data-testid={TestComponentSelectors.BUTTON_FJERN_ALLE}
						text="Fjern alle"
						onClick={handleFjernAlle}
						disabled={isProcessing}
					/>
					<ExpandButton expanded={shouldOpen} onClick={handleTogglePanel} />
				</span>
			</div>
			{shouldOpen && <div className="dolly-panel-content">{renderContent}</div>}
		</div>
	)
}
