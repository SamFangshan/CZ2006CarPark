from onemapsg import OneMapClient

class OneMapConverter:
    def __init__(self, email, password):
        self.email = email
        self.password = password
        self.client = OneMapClient(email, password)

    def convert(self, x, y):
        result = self.client.SVY21_to_WGS84([x, y])
        return result['latitude'], result['longitude']
