import './TidligereBestillinger.less'
import Button from '@/components/ui/button/Button'
import styled from 'styled-components'
import { useDispatch } from 'react-redux'
import { navigerTilBestilling } from '@/ducks/finnPerson'
import { CypressSelector } from '../../../../../cypress/mocks/Selectors'

const NavigerTilBestillingButton = styled(Button)`
	align-self: center;
`

export const TidligereBestillinger = ({ ids }) => {
	const dispatch = useDispatch()

	return (
		<div className="tidligere-bestilling-panel">
			<h4>{`Bestilling-ID${ids.length > 1 ? 'er' : ''}`}</h4>
			{ids
				.map((numId) => numId.toString())
				.map((id) => (
					<NavigerTilBestillingButton
						data-cy={CypressSelector.BUTTON_TIDLIGEREBESTILLINGER_NAVIGER}
						key={id}
						onClick={() => {
							dispatch(navigerTilBestilling(id))
						}}
					>
						{id}
					</NavigerTilBestillingButton>
				))}
		</div>
	)
}
