import './TidligereBestillinger.less'
import Button from '@/components/ui/button/Button'
import styled from 'styled-components'
import { useDispatch } from 'react-redux'
import { navigerTilBestilling } from '@/ducks/finnPerson'
import { TestComponentSelectors } from '#/mocks/Selectors'

const NavigerTilBestillingButton = styled(Button)`
	align-self: center;
	font-size: 15px;
`

export const TidligereBestillinger = ({ ids, erOrg }) => {
	const dispatch = useDispatch()
	const bestillingIds =
		ids
			?.filter((id) => id !== null && id !== undefined && String(id).trim().length > 0)
			?.map((numId) => numId.toString()) || []

	if (bestillingIds.length === 0) {
		return null
	}

	return (
		<div className="tidligere-bestilling-panel">
			<h4>{`Bestilling-ID${bestillingIds.length > 1 ? 'er' : ''}`}</h4>
			{bestillingIds.map((id) =>
				erOrg ? (
					<p key={id}>{id}</p>
				) : (
					<NavigerTilBestillingButton
						data-testid={TestComponentSelectors.BUTTON_TIDLIGEREBESTILLINGER_NAVIGER}
						key={id}
						onClick={() => {
							dispatch(navigerTilBestilling(id))
						}}
					>
						{id}
					</NavigerTilBestillingButton>
				),
			)}
		</div>
	)
}
