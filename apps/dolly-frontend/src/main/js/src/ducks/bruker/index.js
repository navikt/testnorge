import { DollyApi } from '~/service/Api'
import { createActions } from 'redux-actions'

export const { addFavorite, removeFavorite } = createActions(
	{
		addFavorite: DollyApi.addFavorite,
		removeFavorite: DollyApi.removeFavorite,
	},
	{ prefix: 'bruker' }
)
